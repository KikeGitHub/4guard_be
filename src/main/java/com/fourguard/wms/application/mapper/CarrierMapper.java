package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateCarrierRequest;
import com.fourguard.wms.application.dto.request.UpdateCarrierRequest;
import com.fourguard.wms.application.dto.response.CarrierResponse;
import com.fourguard.wms.domain.enums.CarrierStatus;
import com.fourguard.wms.domain.enums.CarrierType;
import com.fourguard.wms.domain.enums.ServiceType;
import com.fourguard.wms.infrastructure.persistence.entity.CarrierEntity;
import com.fourguard.wms.infrastructure.persistence.entity.ClientEntity;
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
    @Mapping(target = "carrierType", source = "carrierType")
    @Mapping(target = "serviceType", source = "serviceType")
    @Mapping(target = "preferredClients", ignore = true)
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
    @Mapping(source = "carrierType", target = "carrierType")
    @Mapping(source = "serviceType", target = "serviceType")
    CarrierResponse toResponse(CarrierEntity entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    CarrierResponse.PreferredClientResponse toPreferredClientResponse(ClientEntity client);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "carrierType", source = "carrierType")
    @Mapping(target = "serviceType", source = "serviceType")
    @Mapping(target = "preferredClients", ignore = true)
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

    default CarrierType mapCarrierType(String value) {
        if (value == null) return CarrierType.EXTERNAL;
        try {
            return CarrierType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CarrierType.EXTERNAL;
        }
    }

    default ServiceType mapServiceType(String value) {
        if (value == null) return ServiceType.FTL;
        try {
            return ServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ServiceType.FTL;
        }
    }
}
