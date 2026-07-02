package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    /** All notifications for a recipient, newest first. */
    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    /** Only unread notifications for a recipient, newest first. */
    List<NotificationEntity> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(UUID recipientId);

    /**
     * Marks a specific notification as read, scoped to the owning recipient
     * to prevent cross-user tampering.
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true " +
           "WHERE n.id = :id AND n.recipientId = :recipientId")
    int markAsRead(@Param("id") UUID id, @Param("recipientId") UUID recipientId);
}
