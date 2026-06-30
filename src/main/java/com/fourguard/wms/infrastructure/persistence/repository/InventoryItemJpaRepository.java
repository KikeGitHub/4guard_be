package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.InventoryItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemEntity, UUID> {

    Optional<InventoryItemEntity> findBySscc(String sscc);

    List<InventoryItemEntity> findByBranchId(UUID branchId);

    Page<InventoryItemEntity> findByBranchId(UUID branchId, Pageable pageable);

    List<InventoryItemEntity> findByLocationId(UUID locationId);

    /** FEFO query — uses idx_inventory_fefo index (state=30=AVAILABLE). */
    @Query("""
            SELECT i FROM InventoryItemEntity i
            WHERE i.sku.id = :skuId AND i.state = com.fourguard.wms.domain.enums.InventoryState.AVAILABLE
            ORDER BY i.expirationDate ASC NULLS LAST
            """)
    List<InventoryItemEntity> findAvailableBySkuOrderedByFefo(UUID skuId);

    boolean existsBySscc(String sscc);
}
