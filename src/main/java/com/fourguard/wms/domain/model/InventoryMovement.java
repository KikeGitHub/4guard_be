package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.MovementType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InventoryMovement {
    private UUID id;
    private InventoryItem item;
    private Location fromLocation;
    private Location toLocation;
    private User user;
    private MovementType type;
    private String reason;
    private OffsetDateTime createdAt;
}
