package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.CarrierStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Carrier {
    private UUID id;
    private Organization organization;
    private String name;
    private String tradeName;
    private String taxId;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private CarrierStatus status;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
