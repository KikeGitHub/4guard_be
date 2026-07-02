package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.UserAdminQueryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter — implements {@link UserAdminQueryPort} using JPA.
 * Finds the primary administrator (role.level = 1) of a given organisation.
 */
@Component
@RequiredArgsConstructor
public class UserAdminQueryAdapter implements UserAdminQueryPort {

    private final UserJpaRepository repository;

    @Override
    public Optional<UserEntity> findTopAdminByOrganizationId(UUID organizationId) {
        return repository.findTopAdminByOrganizationId(organizationId);
    }
}
