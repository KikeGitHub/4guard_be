package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.response.audit.ActiveSessionResponse;
import com.fourguard.wms.infrastructure.persistence.entity.AuditLogEntity;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting {@link UserEntity} and {@link AuditLogEntity} to {@link ActiveSessionResponse}.
 */
@Mapper(componentModel = "spring")
public interface ActiveSessionMapper {

    @Mapping(target = "userId",           source = "user.id")
    @Mapping(target = "username",         source = "user.username")
    @Mapping(target = "email",            source = "user.email")
    @Mapping(target = "organizationId",   source = "user.organization.id")
    @Mapping(target = "organizationName", source = "user.organization.name")
    @Mapping(target = "branchId",         source = "user.branch.id")
    @Mapping(target = "branchName",       source = "user.branch.name")
    @Mapping(target = "lastLoginAt",      source = "loginEvent.createdAt")
    @Mapping(target = "ipAddress",        source = "loginEvent.ipAddress")
    @Mapping(target = "userAgent",        source = "loginEvent.userAgent")
    @Mapping(target = "fullName",         expression = "java(buildFullName(user))")
    ActiveSessionResponse toActiveSessionResponse(UserEntity user, AuditLogEntity loginEvent);

    default String buildFullName(UserEntity user) {
        if (user == null) return null;
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last  = user.getLastName()  != null ? user.getLastName()  : "";
        return (first + " " + last).trim();
    }
}
