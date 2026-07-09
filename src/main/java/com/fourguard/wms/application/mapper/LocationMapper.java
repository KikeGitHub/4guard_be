package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateLocationRequest;
import com.fourguard.wms.application.dto.request.UpdateLocationRequest;
import com.fourguard.wms.application.dto.response.LocationResponse;
import com.fourguard.wms.infrastructure.persistence.entity.LocationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "currentOccupancy", ignore = true)
    @Mapping(target = "isBlocked", ignore = true)
    @Mapping(target = "blockReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "capacityUnits", expression = "java(request.getCapacityUnits() != null ? request.getCapacityUnits() : 1)")
    LocationEntity toEntity(CreateLocationRequest request);

    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.name", target = "branchName")
    @Mapping(source = "section.id", target = "sectionId")
    @Mapping(source = "section.name", target = "sectionName")
    LocationResponse toResponse(LocationEntity entity);

    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "currentOccupancy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(UpdateLocationRequest request, @MappingTarget LocationEntity entity);
}
