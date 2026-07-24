-- =============================================================================
-- V15: Refactor organization settings to remove JSONB
-- Author: 4GUARD Engineering Team
-- Description: Migra las configuraciones de organizaciones del campo JSONB
--              a la tabla relacional wms.organization_settings y elimina la columna settings de wms.organizations.
-- =============================================================================

SET search_path TO wms, public;

-- 1. Crear tabla relacional pura wms.organization_settings
CREATE TABLE IF NOT EXISTS wms.organization_settings (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID            NOT NULL REFERENCES wms.organizations(id) ON DELETE CASCADE,
    setting_key     VARCHAR(100)    NOT NULL,
    setting_value   TEXT,
    CONSTRAINT uk_org_setting_key UNIQUE (organization_id, setting_key)
);

-- 2. Crear índice para optimizar consultas por organization_id
CREATE INDEX IF NOT EXISTS idx_org_settings_org_id ON wms.organization_settings (organization_id);

-- 3. Migrar datos existentes del JSONB a la tabla relacional sin pérdida de datos
INSERT INTO wms.organization_settings (id, organization_id, setting_key, setting_value)
SELECT 
    uuid_generate_v4(),
    o.id,
    kv.key,
    kv.value
FROM wms.organizations o,
LATERAL jsonb_each_text(COALESCE(o.settings, '{}'::jsonb)) AS kv
ON CONFLICT (organization_id, setting_key) 
DO UPDATE SET setting_value = EXCLUDED.setting_value;

-- 4. Eliminar columna JSONB settings de wms.organizations
ALTER TABLE wms.organizations DROP COLUMN IF EXISTS settings;
