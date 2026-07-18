-- =============================================================================
-- V7: HU-128 Gestión de Transportistas + Metadatos Relacionales
-- Author: 4GUARD Engineering Team (AI Assistant)
-- Description: Crea las tablas de transportistas, metadatos de vehículos y sellos,
--              reemplazando el uso de JSONB. Adicionalmente registra los nuevos
--              permisos y los mapea a los roles correspondientes.
-- =============================================================================

SET search_path TO wms, public;

-- 1. Tabla wms.carriers: Catálogo maestro de transportistas
CREATE TABLE wms.carriers (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID         NOT NULL REFERENCES wms.organizations(id),
    name            VARCHAR(200) NOT NULL,
    trade_name      VARCHAR(200),
    tax_id          VARCHAR(30),
    contact_name    VARCHAR(150),
    contact_phone   VARCHAR(20),
    contact_email   VARCHAR(255),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    version         BIGINT       NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),

    UNIQUE (organization_id, name),
    CONSTRAINT chk_carrier_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_carrier_name CHECK (char_length(name) >= 2),
    CONSTRAINT chk_carrier_phone CHECK (contact_phone IS NULL OR char_length(contact_phone) >= 7)
);

-- Trigger de updated_at para carriers
CREATE TRIGGER trg_update_carriers
    BEFORE UPDATE ON wms.carriers
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- Índices de carriers
CREATE INDEX idx_carriers_org ON wms.carriers (organization_id);
CREATE INDEX idx_carriers_org_status ON wms.carriers (organization_id, status);

-- 2. Tabla wms.carrier_vehicle_metadata: Datos del vehículo (reemplaza JSONB de items)
CREATE TABLE wms.carrier_vehicle_metadata (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id         UUID         NOT NULL REFERENCES wms.inventory_items(id) ON DELETE CASCADE,
    carrier_id      UUID         REFERENCES wms.carriers(id) ON DELETE SET NULL,
    vehicle_plates  VARCHAR(20)  NOT NULL,
    driver_name     VARCHAR(150),
    driver_license  VARCHAR(30),
    seal_count      INTEGER      DEFAULT 0,
    operation_type  VARCHAR(20)  NOT NULL DEFAULT 'RECEIVING',
    registered_by   UUID         NOT NULL REFERENCES wms.users(id),
    registered_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    notes           TEXT,
    version         BIGINT       NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),

    CONSTRAINT chk_cvm_seal_count CHECK (seal_count >= 0),
    CONSTRAINT chk_cvm_op_type CHECK (operation_type IN ('RECEIVING', 'SHIPPING')),
    CONSTRAINT chk_cvm_plates CHECK (char_length(vehicle_plates) >= 5)
);

-- Trigger de updated_at para carrier_vehicle_metadata
CREATE TRIGGER trg_update_carrier_vehicle_metadata
    BEFORE UPDATE ON wms.carrier_vehicle_metadata
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- Índices de carrier_vehicle_metadata
CREATE INDEX idx_cvm_item_id ON wms.carrier_vehicle_metadata (item_id);
CREATE INDEX idx_cvm_carrier_id ON wms.carrier_vehicle_metadata (carrier_id);

-- 3. Tabla wms.vehicle_seals: Relación 1:N de sellos
CREATE TABLE wms.vehicle_seals (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    metadata_id     UUID        NOT NULL REFERENCES wms.carrier_vehicle_metadata(id) ON DELETE CASCADE,
    seal_number     VARCHAR(50) NOT NULL,
    seal_type       VARCHAR(30) DEFAULT 'STANDARD',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),

    CONSTRAINT chk_seal_number CHECK (char_length(seal_number) >= 3)
);

-- Índices de vehicle_seals
CREATE INDEX idx_seals_metadata_id ON wms.vehicle_seals (metadata_id);

-- 4. Registro de permisos en wms.permissions
INSERT INTO wms.permissions (id, name, description) VALUES
    (uuid_generate_v4(), 'CARRIERS_READ',   'Permite leer el catálogo de transportistas'),
    (uuid_generate_v4(), 'CARRIERS_CREATE', 'Permite crear transportistas en el catálogo'),
    (uuid_generate_v4(), 'CARRIERS_UPDATE', 'Permite actualizar transportistas del catálogo'),
    (uuid_generate_v4(), 'CARRIERS_DELETE', 'Permite eliminar transportistas del catálogo')
ON CONFLICT (name) DO NOTHING;

-- 5. Mapeo de permisos por rol

-- OPERATIONS_MANAGER: todos los permisos CARRIERS_*
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_MANAGER'
  AND p.name IN ('CARRIERS_READ','CARRIERS_CREATE','CARRIERS_UPDATE','CARRIERS_DELETE')
ON CONFLICT DO NOTHING;

-- CEO: solo lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'CARRIERS_READ'
WHERE r.name = 'CEO' ON CONFLICT DO NOTHING;

-- OPERATIONS_SUPERVISOR: lectura + actualización
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p
    ON p.name IN ('CARRIERS_READ', 'CARRIERS_UPDATE')
WHERE r.name = 'OPERATIONS_SUPERVISOR' ON CONFLICT DO NOTHING;

-- CONTROL_DESK: lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'CARRIERS_READ'
WHERE r.name = 'CONTROL_DESK' ON CONFLICT DO NOTHING;

-- SHIFT_LEADER: lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'CARRIERS_READ'
WHERE r.name = 'SHIFT_LEADER' ON CONFLICT DO NOTHING;
