-- =============================================================================
-- V4: Login Lockout & In-App Notifications
-- Description: Adds failed-login tracking fields to wms.users and creates
--              wms.notifications table for in-app alert delivery.
-- =============================================================================

SET search_path TO wms, public;

-- ─── 1. Add lockout columns to wms.users ─────────────────────────────────────

ALTER TABLE wms.users
    ADD COLUMN IF NOT EXISTS failed_attempts    INTEGER     NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked_until       TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS permanently_locked BOOLEAN     NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN wms.users.failed_attempts    IS 'Number of consecutive failed login attempts in the current window.';
COMMENT ON COLUMN wms.users.locked_until       IS 'Timestamp until which the account is temporarily locked. NULL = not locked.';
COMMENT ON COLUMN wms.users.permanently_locked IS 'When TRUE the account is permanently locked and requires admin intervention.';

-- Supporting index for admin-lookup queries (find level-1 user per organization)
CREATE INDEX IF NOT EXISTS idx_users_org_role ON wms.users (organization_id, role_id);

-- ─── 2. Create wms.notifications table ───────────────────────────────────────

CREATE TABLE IF NOT EXISTS wms.notifications (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID         NOT NULL REFERENCES wms.organizations(id) ON DELETE CASCADE,
    recipient_id    UUID                  REFERENCES wms.users(id)         ON DELETE SET NULL,
    type            VARCHAR(50)  NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT         NOT NULL,
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    metadata        JSONB                 DEFAULT '{}',
    created_at      TIMESTAMPTZ           DEFAULT NOW()
);

COMMENT ON TABLE  wms.notifications               IS 'In-app notification messages sent to users and administrators.';
COMMENT ON COLUMN wms.notifications.type          IS 'Notification category: ACCOUNT_TEMP_LOCKED | ACCOUNT_PERM_LOCKED.';
COMMENT ON COLUMN wms.notifications.recipient_id  IS 'Target user; NULL means the record targets all org admins (not currently used).';
COMMENT ON COLUMN wms.notifications.metadata      IS 'Arbitrary key-value context attached to the notification (e.g. username, IP).';

CREATE INDEX IF NOT EXISTS idx_notifications_recipient
    ON wms.notifications (recipient_id, is_read, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_notifications_org
    ON wms.notifications (organization_id, created_at DESC);
