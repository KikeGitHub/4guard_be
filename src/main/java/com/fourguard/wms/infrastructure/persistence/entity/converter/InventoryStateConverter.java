package com.fourguard.wms.infrastructure.persistence.entity.converter;

import com.fourguard.wms.domain.enums.InventoryState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter — maps {@link InventoryState} ↔ {@code INTEGER}.
 *
 * <p>The {@code inventory_items.state} column uses non-sequential integer codes
 * (10, 20, 30, 40, 50, 60, 70, 80) that do not map to JPA's standard
 * {@code @Enumerated(ORDINAL)}. This converter handles the translation.</p>
 *
 * <p>{@code autoApply = true} means this converter is automatically applied
 * to all entity fields of type {@link InventoryState} without needing
 * explicit {@code @Convert} annotations.</p>
 */
@Converter(autoApply = true)
public class InventoryStateConverter implements AttributeConverter<InventoryState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(InventoryState attribute) {
        return (attribute == null) ? null : attribute.getDbValue();
    }

    @Override
    public InventoryState convertToEntityAttribute(Integer dbData) {
        return (dbData == null) ? null : InventoryState.fromDbValue(dbData);
    }
}
