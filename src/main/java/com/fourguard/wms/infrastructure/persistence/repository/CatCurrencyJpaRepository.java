package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.CatCurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatCurrencyJpaRepository extends JpaRepository<CatCurrencyEntity, String> {
    List<CatCurrencyEntity> findByActiveTrueOrderByCodeAsc();
}
