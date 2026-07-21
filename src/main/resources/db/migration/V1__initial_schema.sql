-- =============================================================================
-- V1: Initial Consolidated Schema
-- Author: 4GUARD Engineering Team (Senior Architect)
-- Description: This single, authoritative script creates the entire initial
--              database schema, ensuring correct creation order, data types,
--              and normalized relationships. All previous migration files
--              have been deprecated in favor of this master script.
-- =============================================================================

-- Create schema and set search path
CREATE SCHEMA IF NOT EXISTS wms;
SET search_path TO wms, public;

-- Required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================================================
-- Section 1: Core Identity and Security Tables (RBAC)
-- =============================================================================

CREATE TABLE wms.organizations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(200) NOT NULL,
    code            VARCHAR(20)  UNIQUE NOT NULL,
    tax_id          VARCHAR(20)  UNIQUE,
    type            VARCHAR(50)  NOT NULL,
    status          VARCHAR(20)  DEFAULT 'ACTIVE',
    settings        JSONB        DEFAULT '{}',
    version         BIGINT       DEFAULT 1,
    created_at      TIMESTAMPTZ  DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE TABLE wms.roles (
    id          UUID    PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(50) UNIQUE NOT NULL,
    level       INTEGER NOT NULL CHECK (level BETWEEN 1 AND 7),
    is_system   BOOLEAN DEFAULT FALSE,
    version     BIGINT  DEFAULT 1,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    updated_at  TIMESTAMPTZ DEFAULT NOW(),
    created_by  VARCHAR(36),
    updated_by  VARCHAR(36)
);

CREATE TABLE wms.permissions (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE wms.role_permissions (
    role_id       UUID NOT NULL REFERENCES wms.roles(id)       ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES wms.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE wms.branches (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID        NOT NULL REFERENCES wms.organizations(id),
    name            VARCHAR(200) NOT NULL,
    code            VARCHAR(20)  NOT NULL,
    timezone        VARCHAR(50)  DEFAULT 'UTC',
    address_line1   TEXT,
    status          VARCHAR(20)  DEFAULT 'ACTIVE',
    version         BIGINT       DEFAULT 1,
    created_at      TIMESTAMPTZ  DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),
    UNIQUE(organization_id, code)
);

CREATE TABLE wms.users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(50)  UNIQUE NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    organization_id UUID         NOT NULL REFERENCES wms.organizations(id),
    branch_id       UUID                  REFERENCES wms.branches(id),
    role_id         UUID         NOT NULL REFERENCES wms.roles(id),
    status          VARCHAR(20)  DEFAULT 'PENDING',
    is_enabled      BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login      TIMESTAMPTZ,
    version         BIGINT       DEFAULT 1,
    created_at      TIMESTAMPTZ  DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

-- =============================================================================
-- Section 2: Warehouse Topology
-- =============================================================================

CREATE TABLE wms.warehouse_sections (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    branch_id   UUID        NOT NULL REFERENCES wms.branches(id),
    code        VARCHAR(10) NOT NULL,
    name        VARCHAR(100),
    version     BIGINT      DEFAULT 1,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    updated_at  TIMESTAMPTZ DEFAULT NOW(),
    created_by  VARCHAR(36),
    updated_by  VARCHAR(36)
);

CREATE TABLE wms.locations (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    branch_id         UUID        NOT NULL REFERENCES wms.branches(id),
    section_id        UUID                  REFERENCES wms.warehouse_sections(id),
    zone              VARCHAR(10) NOT NULL,
    aisle             VARCHAR(10),
    rack              VARCHAR(10),
    level             INTEGER,
    position          VARCHAR(10),
    coord_x           INTEGER,
    coord_y           INTEGER,
    coord_z           INTEGER,
    type              VARCHAR(20) CHECK (type IN ('PALLET', 'BIN', 'SHELF', 'RAMP')),
    capacity_units    INTEGER     DEFAULT 1,
    current_occupancy INTEGER     DEFAULT 0,
    is_blocked        BOOLEAN     DEFAULT FALSE,
    block_reason      TEXT,
    version           BIGINT      DEFAULT 1,
    created_at        TIMESTAMPTZ DEFAULT NOW(),
    updated_at        TIMESTAMPTZ DEFAULT NOW(),
    created_by        VARCHAR(36),
    updated_by        VARCHAR(36)
);

-- =============================================================================
-- Section 3: Merchandise and Inventory
-- =============================================================================

CREATE TABLE wms.clients (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID         NOT NULL REFERENCES wms.organizations(id),
    name            VARCHAR(200) NOT NULL,
    external_id     VARCHAR(50),
    status          VARCHAR(20)  DEFAULT 'ACTIVE',
    version         BIGINT       DEFAULT 1,
    created_at      TIMESTAMPTZ  DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE TABLE wms.products_sku (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id   UUID            NOT NULL REFERENCES wms.clients(id),
    code        VARCHAR(50)     NOT NULL,
    name        VARCHAR(200)    NOT NULL,
    description TEXT,
    weight      DECIMAL(10,3),
    unit        VARCHAR(20)     NOT NULL,
    version     BIGINT          DEFAULT 1,
    created_at  TIMESTAMPTZ     DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     DEFAULT NOW(),
    created_by  VARCHAR(36),
    updated_by  VARCHAR(36)
);

CREATE TABLE wms.inventory_items (
    id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id      UUID            NOT NULL REFERENCES wms.organizations(id),
    branch_id            UUID            NOT NULL REFERENCES wms.branches(id),
    client_id            UUID            NOT NULL REFERENCES wms.clients(id),
    sscc                 VARCHAR(20)     UNIQUE NOT NULL,
    external_ua          VARCHAR(20),
    sku_id               UUID            NOT NULL REFERENCES wms.products_sku(id),
    location_id          UUID                     REFERENCES wms.locations(id),
    state                INTEGER         NOT NULL CHECK (state IN (10, 20, 30, 40, 50, 60, 70, 80)),
    quantity             DECIMAL(12,3)   NOT NULL DEFAULT 0,
    batch_number         VARCHAR(50),
    manufacturing_date   DATE,
    expiration_date      DATE,
    sap_folio            VARCHAR(50),
    quarantine_reason    TEXT,
    metadata             JSONB           DEFAULT '{}',
    version              BIGINT          NOT NULL DEFAULT 1,
    created_at           TIMESTAMPTZ     DEFAULT NOW(),
    updated_at           TIMESTAMPTZ     DEFAULT NOW(),
    created_by           VARCHAR(36),
    updated_by           VARCHAR(36)
);

CREATE TABLE wms.inventory_movements (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id          UUID        NOT NULL REFERENCES wms.inventory_items(id),
    from_location_id UUID                  REFERENCES wms.locations(id),
    to_location_id   UUID                  REFERENCES wms.locations(id),
    user_id          UUID        NOT NULL REFERENCES wms.users(id),
    type             VARCHAR(50) NOT NULL,
    reason           TEXT,
    created_at       TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================================================
-- Section 4: Quality and Audit
-- =============================================================================

CREATE TABLE wms.incidences (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    folio          SERIAL UNIQUE,
    item_id        UUID        NOT NULL REFERENCES wms.inventory_items(id),
    type           VARCHAR(50) NOT NULL,
    severity       VARCHAR(20) CHECK (severity IN ('RED', 'YELLOW', 'BLUE')),
    reported_by_id UUID                  REFERENCES wms.users(id),
    status         VARCHAR(20) DEFAULT 'OPEN',
    created_at     TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE wms.audit_logs (
    log_id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID         NOT NULL REFERENCES wms.organizations(id),
    branch_id       UUID                   REFERENCES wms.branches(id),
    user_id         UUID         NOT NULL REFERENCES wms.users(id),
    action          VARCHAR(100) NOT NULL,
    entity_type     VARCHAR(50)  NOT NULL,
    entity_id       UUID         NOT NULL,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE wms.audit_log_details (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    log_id      UUID NOT NULL REFERENCES wms.audit_logs(log_id) ON DELETE CASCADE,
    field_name  VARCHAR(100) NOT NULL,
    old_value   TEXT,
    new_value   TEXT
);

-- =============================================================================
-- Section 5: Triggers and Indexes
-- =============================================================================

CREATE OR REPLACE FUNCTION wms.protect_audit_logs()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Operation not permitted: this table is immutable (WORM)';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION wms.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    NEW.version    = OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_inventory
    BEFORE UPDATE ON wms.inventory_items
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

CREATE TRIGGER trg_update_users
    BEFORE UPDATE ON wms.users
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

CREATE TRIGGER trg_audit_logs_worm
    BEFORE UPDATE OR DELETE ON wms.audit_logs
    FOR EACH ROW EXECUTE FUNCTION wms.protect_audit_logs();

CREATE INDEX idx_inventory_sscc   ON wms.inventory_items (sscc);
CREATE INDEX idx_inventory_fefo   ON wms.inventory_items (sku_id, expiration_date) WHERE state = 30;
CREATE INDEX idx_audit_created    ON wms.audit_logs (created_at DESC);
