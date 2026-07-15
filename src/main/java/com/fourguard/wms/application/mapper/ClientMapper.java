package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateClientRequest;
import com.fourguard.wms.application.dto.request.UpdateClientRequest;
import com.fourguard.wms.application.dto.response.ClientResponse;
import com.fourguard.wms.infrastructure.persistence.entity.ClientEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    /**
     * Mapea CreateClientRequest → ClientEntity.
     * - organizationName del request es solo campo referencial (no existe en ClientEntity), MapStruct lo ignora por unmapped source.
     * - version y status se manejan en @AfterMapping.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    ClientEntity toEntity(CreateClientRequest request);

    /**
     * POST: Aplica status (default ACTIVE) y version (default 1) después del mapeo.
     */
    @AfterMapping
    default void applyDefaultsOnCreate(CreateClientRequest request, @MappingTarget ClientEntity entity) {
        entity.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        entity.setVersion(request.getVersion() != null ? request.getVersion() : 1L);
    }

    /**
     * Mapea ClientEntity → ClientResponse (con organizationId y organizationName planos).
     */
    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    ClientResponse toResponse(ClientEntity entity);

    /**
     * Mapea UpdateClientRequest → ClientEntity (solo campos comunes, sin status/version).
     * - organizationName del request es solo campo referencial, no existe en ClientEntity.
     * - status y version se manejan condicionalmente en @AfterMapping.
     */
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDto(UpdateClientRequest request, @MappingTarget ClientEntity entity);

    /**
     * PUT: Aplica status y version solo si el request los manda; si no, conserva los valores actuales de BD.
     */
    @AfterMapping
    default void applyConditionalFieldsOnUpdate(UpdateClientRequest request, @MappingTarget ClientEntity entity) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            entity.setStatus(request.getStatus());
        }
        if (request.getVersion() != null) {
            entity.setVersion(request.getVersion());
        }
    }
}
