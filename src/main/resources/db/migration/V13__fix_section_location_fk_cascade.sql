-- =============================================================================
-- V13: Fix Warehouse Sections & Locations FK Cascade Constraint
-- Author: 4GUARD Engineering Team
-- Description: Updates locations_section_id_fkey to ON DELETE SET NULL
-- =============================================================================

SET search_path TO wms, public;

ALTER TABLE wms.locations
    DROP CONSTRAINT IF EXISTS locations_section_id_fkey;

ALTER TABLE wms.locations
    ADD CONSTRAINT locations_section_id_fkey
        FOREIGN KEY (section_id) REFERENCES wms.warehouse_sections(id)
        ON DELETE SET NULL;
