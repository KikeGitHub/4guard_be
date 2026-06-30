package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateOrganizationRequest;
import com.fourguard.wms.application.dto.response.OrganizationResponse;
import com.fourguard.wms.domain.enums.OrganizationStatus;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "version",   ignore = true)
    @Mapping(target = "branches",  ignore = true)
    @Mapping(target = "clients",   ignore = true)
    @Mapping(target = "users",     ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "status",    constant = "ACTIVE")
    OrganizationEntity toEntity(CreateOrganizationRequest request);

    OrganizationResponse toResponse(OrganizationEntity entity);
}
