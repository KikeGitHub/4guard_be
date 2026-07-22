package com.fourguard.wms.domain.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Domain model for the contact record of a Supplier (1:1). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SupplierContact {
    private UUID id;
    private String fullName;
    private String jobTitle;
    private String email;
    private String phone;
    private String altPhone;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
