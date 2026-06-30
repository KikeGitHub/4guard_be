package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    @Override public Optional<UserEntity> findById(UUID id)                   { return repository.findById(id); }
    @Override public List<UserEntity>     findAll()                           { return repository.findAll(); } // Implementation for findAll
    @Override public Optional<UserEntity> findByUsername(String u)            { return repository.findByUsername(u); }
    @Override public Optional<UserEntity> findByEmail(String e)               { return repository.findByEmail(e); }
    @Override public Optional<UserEntity> findByUsernameOrEmail(String i)     { return repository.findByUsernameOrEmail(i); }
    @Override public List<UserEntity>     findByOrganizationId(UUID orgId)    { return repository.findByOrganizationId(orgId); }
    @Override public UserEntity           save(UserEntity user)               { return repository.save(user); }
    @Override public void                 deleteById(UUID id)                 { repository.deleteById(id); } // Implementation for deleteById
    @Override public boolean              existsByUsername(String u)          { return repository.existsByUsername(u); }
    @Override public boolean              existsByEmail(String e)             { return repository.existsByEmail(e); }
}