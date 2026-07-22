package com.fourguard.wms.domain.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Domain model for the address record of a Supplier (1:1, optional). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SupplierAddress {
    private UUID id;
    private String country;
    private String state;
    private String municipality;
    private String city;
    private String postalCode;
    private String street;
    private String exteriorNumber;
    private String interiorNumber;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
