-- =============================================================================
-- V12: Warehouse Section Status Column
-- Author: 4GUARD Engineering Team
-- Description: Adds status column to wms.warehouse_sections (ACTIVE | INACTIVE)
-- =============================================================================

SET search_path TO wms, public;

ALTER TABLE wms.warehouse_sections
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'INACTIVE'));

CREATE INDEX IF NOT EXISTS idx_warehouse_sections_status
    ON wms.warehouse_sections (status);
