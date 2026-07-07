package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateBranchRequest;
import com.fourguard.wms.application.dto.request.UpdateBranchRequest;
import com.fourguard.wms.application.dto.response.BranchResponse;
import com.fourguard.wms.infrastructure.persistence.entity.BranchEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus() : com.fourguard.wms.domain.enums.BranchStatus.ACTIVE)")
    @Mapping(target = "timezone", defaultValue = "UTC")
    BranchEntity toEntity(CreateBranchRequest request);

    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    BranchResponse toResponse(BranchEntity entity);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(UpdateBranchRequest request, @MappingTarget BranchEntity entity);
}
