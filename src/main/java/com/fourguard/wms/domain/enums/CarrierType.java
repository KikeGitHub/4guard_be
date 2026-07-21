package com.fourguard.wms.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Type of carrier company.
 * Mapped to the Spanish terms used in the UI.
 */
@Getter
@RequiredArgsConstructor
public enum CarrierType {
    EXTERNAL("Transportista externo"),
    CLIENT_TRANSPORT("Transporte del cliente"),
    OWN_TRANSPORT("Transporte propio (Interno)"),
    THIRD_PARTY_3PL("Tercerizado 3PL"),
    PARCEL("Paquetería");

    private final String description;
}
