-- =============================================================================
-- V5: Set change_password_required default to TRUE
-- Description: Ensures new user records require password change by default.
-- =============================================================================

SET search_path TO wms, public;

ALTER TABLE wms.users
    ALTER COLUMN change_password_required SET DEFAULT TRUE;
