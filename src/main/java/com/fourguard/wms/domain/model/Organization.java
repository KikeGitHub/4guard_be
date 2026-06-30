package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.OrganizationStatus;
import com.fourguard.wms.domain.enums.OrganizationType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Organization {
    private UUID id;
    private String name;
    private String code;
    private String taxId;
    private OrganizationType type;
    private OrganizationStatus status;
    private Map<String, Object> settings;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
