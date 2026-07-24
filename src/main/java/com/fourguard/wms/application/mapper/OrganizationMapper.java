package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateOrganizationRequest;
import com.fourguard.wms.application.dto.request.UpdateOrganizationRequest;
import com.fourguard.wms.application.dto.response.OrganizationResponse;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationSettingEntity;
import org.mapstruct.*;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class OrganizationMapper {

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
    @Mapping(target = "settings",  source = "settings", qualifiedByName = "mapToSettingEntities")
    public abstract OrganizationEntity toEntity(CreateOrganizationRequest request);

    @AfterMapping
    protected void linkSettings(@MappingTarget OrganizationEntity entity) {
        if (entity.getSettings() != null) {
            entity.getSettings().forEach(setting -> setting.setOrganization(entity));
        }
    }

    @Mapping(target = "settings", source = "settings", qualifiedByName = "mapToSettingsMap")
    public abstract OrganizationResponse toResponse(OrganizationEntity entity);

    @Mapping(target = "branches",  ignore = true)
    @Mapping(target = "clients",   ignore = true)
    @Mapping(target = "users",     ignore = true)
    @Mapping(target = "version",   ignore = true)
    @Mapping(target = "code",      ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "settings",  ignore = true)
    public abstract void updateEntityFromDto(UpdateOrganizationRequest request, @MappingTarget OrganizationEntity entity);

    @AfterMapping
    protected void updateSettingsFromDto(UpdateOrganizationRequest request, @MappingTarget OrganizationEntity entity) {
        if (request.getSettings() != null) {
            Map<String, Object> newSettingsMap = request.getSettings();
            if (entity.getSettings() == null) {
                entity.setSettings(new ArrayList<>());
            }

            List<OrganizationSettingEntity> currentList = entity.getSettings();

            // 1. Remover elementos que ya no existen en la nueva petición
            currentList.removeIf(setting -> !newSettingsMap.containsKey(setting.getSettingKey()));

            // 2. Actualizar valores existentes in-place o agregar nuevos
            for (Map.Entry<String, Object> entry : newSettingsMap.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue() != null ? String.valueOf(entry.getValue()) : null;

                Optional<OrganizationSettingEntity> existingSettingOpt = currentList.stream()
                        .filter(s -> key.equals(s.getSettingKey()))
                        .findFirst();

                if (existingSettingOpt.isPresent()) {
                    existingSettingOpt.get().setSettingValue(val);
                } else {
                    currentList.add(OrganizationSettingEntity.builder()
                            .organization(entity)
                            .settingKey(key)
                            .settingValue(val)
                            .build());
                }
            }
        }
    }

    @Named("mapToSettingEntities")
    protected List<OrganizationSettingEntity> mapToSettingEntities(Map<String, Object> map) {
        if (map == null) return new ArrayList<>();
        return map.entrySet().stream()
                .map(e -> OrganizationSettingEntity.builder()
                        .settingKey(e.getKey())
                        .settingValue(e.getValue() != null ? String.valueOf(e.getValue()) : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapToSettingsMap")
    protected Map<String, Object> mapToSettingsMap(List<OrganizationSettingEntity> list) {
        if (list == null) return Collections.emptyMap();
        Map<String, Object> map = new HashMap<>();
        for (OrganizationSettingEntity setting : list) {
            map.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return map;
    }
}
