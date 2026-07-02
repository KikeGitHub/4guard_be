package com.fourguard.wms.application.usecase;

import com.fourguard.wms.domain.enums.UserStatus;
import com.fourguard.wms.domain.exception.AccountPermanentlyLockedException;
import com.fourguard.wms.domain.exception.AccountTemporarilyLockedException;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.shared.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Service responsible for enforcing the account lockout policy on failed logins.
 *
 * <h2>Policy</h2>
 * <ul>
 *   <li>3 wrong passwords within any window → temporary lock for 15 minutes.</li>
 *   <li>3 more failures after the temporary lock expires → permanent lock.</li>
 *   <li>Both events trigger in-app notifications for the user and the org admin.</li>
 * </ul>
 *
 * <p>This service is called exclusively from {@link LoginUseCaseImpl}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLockService {

    /** Maximum consecutive failures before triggering a lockout. */
    private static final int  MAX_ATTEMPTS  = 3;
    /** Duration of a temporary lockout in minutes. */
    private static final int  LOCK_MINUTES  = 15;

    private final UserRepositoryPort  userRepositoryPort;
    private final NotificationService notificationService;

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Checks whether the account is currently locked BEFORE validating the password.
     * Must be called before any credential comparison to avoid leaking timing information.
     *
     * @param user the candidate account retrieved from the repository
     * @throws AccountPermanentlyLockedException  if the account is permanently blocked
     * @throws AccountTemporarilyLockedException  if the account is still within its lock window
     */
    public void checkLockStatus(UserEntity user) {
        if (Boolean.TRUE.equals(user.getPermanentlyLocked())) {
            log.warn("[LOCKOUT] Rejected login — permanently locked: {}", user.getUsername());
            throw new AccountPermanentlyLockedException();
        }

        if (isStillTemporarilyLocked(user)) {
            long minutesLeft = ChronoUnit.MINUTES.between(
                    OffsetDateTime.now(ZoneOffset.UTC), user.getLockedUntil()) + 1;
            log.warn("[LOCKOUT] Rejected login — temporarily locked ({} min left): {}",
                     minutesLeft, user.getUsername());
            throw new AccountTemporarilyLockedException(minutesLeft);
        }
    }

    /**
     * Registers a failed login attempt and applies the appropriate lockout if the
     * configured threshold is reached.
     *
     * <p>Must be called AFTER a password mismatch is detected.</p>
     *
     * @param user the account that produced a wrong password
     */
    @Transactional
    public void registerFailedAttempt(UserEntity user) {
        boolean tempLockExpired = hasTempLockExpired(user);

        // If the temporary lock has just expired, restart the attempt counter cleanly.
        if (tempLockExpired) {
            log.info("[LOCKOUT] Temp lock expired — resetting attempt counter for: {}",
                     user.getUsername());
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
        }

        int current = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
        int newCount = current + 1;
        user.setFailedAttempts(newCount);

        log.info("[LOCKOUT] Failed attempt {}/{} for user: {}", newCount, MAX_ATTEMPTS,
                 user.getUsername());

        if (newCount >= MAX_ATTEMPTS) {
            if (tempLockExpired) {
                // The user already had a temporary lock that expired; this is the second strike.
                applyPermanentLock(user);
            } else {
                applyTemporaryLock(user);
            }
            // Reset attempt counter after locking so the next window starts fresh.
            user.setFailedAttempts(0);
        }

        userRepositoryPort.save(user);
    }

    /**
     * Resets the failed-attempt counter on a successful login.
     * Only writes to the database when there is something to reset.
     *
     * @param user the account that just authenticated successfully
     */
    @Transactional
    public void resetOnSuccess(UserEntity user) {
        if ((user.getFailedAttempts() != null && user.getFailedAttempts() > 0)
                || user.getLockedUntil() != null) {
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            userRepositoryPort.save(user);
            log.info("[LOCKOUT] Attempt counter reset after successful login: {}",
                     user.getUsername());
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private boolean isStillTemporarilyLocked(UserEntity user) {
        return user.getLockedUntil() != null
                && OffsetDateTime.now(ZoneOffset.UTC).isBefore(user.getLockedUntil());
    }

    private boolean hasTempLockExpired(UserEntity user) {
        return user.getLockedUntil() != null
                && OffsetDateTime.now(ZoneOffset.UTC).isAfter(user.getLockedUntil());
    }

    private void applyTemporaryLock(UserEntity user) {
        OffsetDateTime until = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(LOCK_MINUTES);
        user.setLockedUntil(until);
        log.warn("[LOCKOUT] Temporary lock applied until {} for user: {}", until,
                 user.getUsername());
        notificationService.notifyTemporaryLock(user);
    }

    private void applyPermanentLock(UserEntity user) {
        user.setPermanentlyLocked(true);
        user.setStatus(UserStatus.SUSPENDED);
        user.setLockedUntil(null);
        log.warn("[LOCKOUT] Permanent lock applied for user: {}", user.getUsername());
        notificationService.notifyPermanentLock(user);
    }
}
