-- =============================================================================
-- V8: Refactor audit_logs to remove JSONB and introduce audit_log_details
-- Author: 4GUARD Engineering Team (AI Assistant)
-- Description: Elimina las columnas JSONB (before_state, after_state) de wms.audit_logs
--              y crea la tabla relacional pura wms.audit_log_details.
-- =============================================================================

SET search_path TO wms, public;

-- 1. Eliminar columnas JSONB de wms.audit_logs si existen
ALTER TABLE wms.audit_logs DROP COLUMN IF EXISTS before_state;
ALTER TABLE wms.audit_logs DROP COLUMN IF EXISTS after_state;

-- 2. Crear tabla relacional pura wms.audit_log_details
CREATE TABLE IF NOT EXISTS wms.audit_log_details (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    log_id      UUID        NOT NULL REFERENCES wms.audit_logs(log_id) ON DELETE CASCADE,
    field_name  VARCHAR(100) NOT NULL,
    old_value   TEXT,
    new_value   TEXT
);

-- 3. Crear índice para acelerar consultas por log_id
CREATE INDEX IF NOT EXISTS idx_ald_log_id ON wms.audit_log_details (log_id);
