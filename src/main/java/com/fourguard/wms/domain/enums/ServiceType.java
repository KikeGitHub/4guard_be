package com.fourguard.wms.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Type of service provided by the carrier.
 * Mapped to the Spanish terms used in the UI.
 */
@Getter
@RequiredArgsConstructor
public enum ServiceType {
    FTL("Carga completa (FTL)"),
    LTL("Carga consolidada (LTL)"),
    PARCEL("Paquetería"),
    INTERMODAL("Intermodal"),
    LAST_MILE("Última milla"),
    DEDICATED("Dedicado");

    private final String description;
}
