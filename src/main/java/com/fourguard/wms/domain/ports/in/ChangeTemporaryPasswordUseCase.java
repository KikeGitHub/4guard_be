package com.fourguard.wms.domain.ports.in;

/** Port of entry for an authenticated operator to change their temporary password to a permanent one. */
public interface ChangeTemporaryPasswordUseCase {
    void changePassword(String username, String newPassword);
}
