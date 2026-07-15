-- =============================================================================
-- V3: Add change_password_required column to users table
-- Author: 4GUARD Engineering Team (Senior Developer)
-- Description: Adds a boolean flag to track if the user has a temporary
--              password and must change it on their next login.
-- =============================================================================

SET search_path TO wms, public;

ALTER TABLE wms.users 
ADD COLUMN IF NOT EXISTS change_password_required BOOLEAN NOT NULL DEFAULT FALSE;
