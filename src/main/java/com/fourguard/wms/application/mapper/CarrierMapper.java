package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateCarrierRequest;
import com.fourguard.wms.application.dto.request.UpdateCarrierRequest;
import com.fourguard.wms.application.dto.response.CarrierResponse;
import com.fourguard.wms.domain.enums.CarrierStatus;
import com.fourguard.wms.infrastructure.persistence.entity.CarrierEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    CarrierEntity toEntity(CreateCarrierRequest request);

    @AfterMapping
    default void applyDefaultsOnCreate(CreateCarrierRequest request, @MappingTarget CarrierEntity entity) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                entity.setStatus(CarrierStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                entity.setStatus(CarrierStatus.ACTIVE);
            }
        } else {
            entity.setStatus(CarrierStatus.ACTIVE);
        }
        entity.setVersion(request.getVersion() != null ? request.getVersion() : 1L);
    }

    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    CarrierResponse toResponse(CarrierEntity entity);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDto(UpdateCarrierRequest request, @MappingTarget CarrierEntity entity);

    @AfterMapping
    default void applyConditionalFieldsOnUpdate(UpdateCarrierRequest request, @MappingTarget CarrierEntity entity) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                entity.setStatus(CarrierStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (request.getVersion() != null) {
            entity.setVersion(request.getVersion());
        }
    }
}
