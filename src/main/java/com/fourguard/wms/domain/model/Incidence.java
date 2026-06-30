package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.IncidenceSeverity;
import com.fourguard.wms.domain.enums.IncidenceStatus;
import com.fourguard.wms.domain.enums.IncidenceType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Incidence {
    private UUID id;
    private Integer folio;
    private InventoryItem item;
    private IncidenceType type;
    private java.time.LocalDateTime createdDate; // wait, let's keep it simple and match properties.
    private IncidenceSeverity severity;
    private User reportedBy;
    private IncidenceStatus status;
    private OffsetDateTime createdAt;
}
