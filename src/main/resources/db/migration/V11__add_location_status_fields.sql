-- =============================================================================
-- V11: Location FSM Status Fields (HU-127)
-- Author: 4GUARD Engineering Team
-- Description: Extends wms.locations with:
--   - code          VARCHAR(30) UNIQUE  — human-readable location code (e.g. ALMC-A-R1-N2)
--   - name          VARCHAR(150)        — descriptive name
--   - status        VARCHAR(20) FSM     — ACTIVE | BLOCKED | MAINTENANCE | INACTIVE
--   - status_reason VARCHAR(300)        — required for BLOCKED and MAINTENANCE states
-- Also installs a trigger that keeps is_blocked / block_reason in sync with status
-- for backwards-compatibility with existing integrations.
-- =============================================================================

SET search_path TO wms, public;

-- ---------------------------------------------------------------------------
-- 1. New columns
-- ---------------------------------------------------------------------------

ALTER TABLE wms.locations
    ADD COLUMN IF NOT EXISTS code VARCHAR(30) UNIQUE;

ALTER TABLE wms.locations
    ADD COLUMN IF NOT EXISTS name VARCHAR(150);

ALTER TABLE wms.locations
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'MAINTENANCE', 'INACTIVE'));

ALTER TABLE wms.locations
    ADD COLUMN IF NOT EXISTS status_reason VARCHAR(300);

-- ---------------------------------------------------------------------------
-- 2. Backfill: rows already flagged as is_blocked=true → status = 'BLOCKED'
-- ---------------------------------------------------------------------------

UPDATE wms.locations
SET    status        = 'BLOCKED',
       status_reason = block_reason
WHERE  is_blocked = TRUE;

-- ---------------------------------------------------------------------------
-- 3. Trigger: keep is_blocked / block_reason in sync with status
--    This runs on every INSERT and UPDATE so the legacy fields are always
--    consistent with the new FSM column.
-- ---------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION wms.sync_location_is_blocked()
RETURNS TRIGGER AS $$
BEGIN
    NEW.is_blocked   := (NEW.status = 'BLOCKED');
    NEW.block_reason := CASE
                            WHEN NEW.status = 'BLOCKED' THEN NEW.status_reason
                            ELSE NULL
                        END;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sync_location_status ON wms.locations;

CREATE TRIGGER trg_sync_location_status
    BEFORE INSERT OR UPDATE ON wms.locations
    FOR EACH ROW EXECUTE FUNCTION wms.sync_location_is_blocked();

-- ---------------------------------------------------------------------------
-- 4. Indexes for common query patterns
-- ---------------------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_locations_status
    ON wms.locations (status);

CREATE INDEX IF NOT EXISTS idx_locations_code
    ON wms.locations (code)
    WHERE code IS NOT NULL;
