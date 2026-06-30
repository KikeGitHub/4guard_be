package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.BranchStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private UUID id;
    private Organization organization;
    private String name;
    private String code;
    private String timezone;
    private String addressLine1;
    private BranchStatus status;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
