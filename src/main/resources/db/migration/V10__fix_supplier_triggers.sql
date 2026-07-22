-- =============================================================================
-- V10: Corrige los triggers de actualización de proveedores (HU-125)
-- Description: Crea una función de trigger para tablas sin columna de versión,
--              evitando el error de "el registro NEW no tiene un campo version".
-- =============================================================================

SET search_path TO wms, public;

-- 1. Crear función de trigger que solo actualice updated_at sin tocar la versión
CREATE OR REPLACE FUNCTION wms.update_updated_at_column_without_version()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2. Eliminar triggers antiguos de tablas sin columna 'version'
DROP TRIGGER IF EXISTS trg_update_supplier_contacts ON wms.supplier_contacts;
DROP TRIGGER IF EXISTS trg_update_supplier_addresses ON wms.supplier_addresses;
DROP TRIGGER IF EXISTS trg_update_supplier_commercial_terms ON wms.supplier_commercial_terms;

-- 3. Volver a crear los triggers con la nueva función sin versión
CREATE TRIGGER trg_update_supplier_contacts
    BEFORE UPDATE ON wms.supplier_contacts
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column_without_version();

CREATE TRIGGER trg_update_supplier_addresses
    BEFORE UPDATE ON wms.supplier_addresses
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column_without_version();

CREATE TRIGGER trg_update_supplier_commercial_terms
    BEFORE UPDATE ON wms.supplier_commercial_terms
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column_without_version();
