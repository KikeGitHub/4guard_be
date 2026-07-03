package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT — Specialised query to locate the top-level administrator of an organisation.
 * The admin is defined as the enabled user whose role has the lowest {@code level} value
 * (level 1 = highest privilege) within the given organisation.
 *
 * <p>Implemented by {@code UserAdminQueryAdapter} in the infrastructure layer.</p>
 */
public interface UserAdminQueryPort {

    /**
     * Finds the primary administrator of the given organisation.
     * Returns {@link Optional#empty()} when no active admin exists.
     *
     * @param organizationId the organisation to search within
     */
    Optional<UserEntity> findTopAdminByOrganizationId(UUID organizationId);
}
