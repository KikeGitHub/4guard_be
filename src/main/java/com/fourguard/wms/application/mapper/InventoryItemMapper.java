package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.response.InventoryItemResponse;
import com.fourguard.wms.infrastructure.persistence.entity.InventoryItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "branchId",       source = "branch.id")
    @Mapping(target = "clientId",       source = "client.id")
    @Mapping(target = "skuId",          source = "sku.id")
    @Mapping(target = "locationId",     source = "location.id")
    InventoryItemResponse toResponse(InventoryItemEntity entity);
}
