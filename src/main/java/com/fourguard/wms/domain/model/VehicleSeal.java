package com.fourguard.wms.domain.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class VehicleSeal {
    private UUID id;
    private UUID metadataId;
    private String sealNumber;
    private String sealType;
    private OffsetDateTime createdAt;
    private String createdBy;
}
