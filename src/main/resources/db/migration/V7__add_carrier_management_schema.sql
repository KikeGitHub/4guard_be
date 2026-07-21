-- =============================================================================
-- V7: HU-128 Gestión de Transportistas + Metadatos Relacionales
-- Author: 4GUARD Engineering Team (AI Assistant)
-- Description: Crea las tablas de transportistas, metadatos de vehículos, sellos,
--              relaciones complejas de la UI (capacidades y clientes preferentes)
--              y siembra datos reales de demo.
-- =============================================================================

SET search_path TO wms, public;

-- 1. Tabla wms.carriers: Catálogo maestro
CREATE TABLE wms.carriers (
    id                   UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id      UUID         NOT NULL REFERENCES wms.organizations(id),
    name                 VARCHAR(200) NOT NULL,
    trade_name           VARCHAR(200) NOT NULL,
    tax_id               VARCHAR(30)  NOT NULL,
    carrier_type         VARCHAR(50)  NOT NULL DEFAULT 'EXTERNAL',
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    contact_name         VARCHAR(150) NOT NULL,
    contact_phone        VARCHAR(20)  NOT NULL,
    contact_email        VARCHAR(255) NOT NULL,
    service_type         VARCHAR(50)  NOT NULL DEFAULT 'FTL',
    permit_number        VARCHAR(100),
    geographic_coverage  TEXT,
    notes                TEXT,
    version              BIGINT       NOT NULL DEFAULT 1,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(36),
    updated_by           VARCHAR(36),

    UNIQUE (organization_id, name),
    CONSTRAINT chk_carrier_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_carrier_type   CHECK (carrier_type IN ('EXTERNAL', 'CLIENT_TRANSPORT', 'OWN_TRANSPORT', 'THIRD_PARTY_3PL', 'PARCEL')),
    CONSTRAINT chk_service_type   CHECK (service_type IN ('FTL', 'LTL', 'PARCEL', 'INTERMODAL', 'LAST_MILE', 'DEDICATED')),
    CONSTRAINT chk_carrier_name   CHECK (char_length(name) >= 2),
    CONSTRAINT chk_carrier_phone  CHECK (char_length(contact_phone) >= 7)
);

-- Trigger de updated_at para wms.carriers
CREATE TRIGGER trg_update_carriers
    BEFORE UPDATE ON wms.carriers
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- 2. Tabla intermedia de capacidades de vehículos (Checkboxes de la UI)
CREATE TABLE wms.carrier_vehicle_types (
    carrier_id   UUID        NOT NULL REFERENCES wms.carriers(id) ON DELETE CASCADE,
    vehicle_type VARCHAR(50) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (carrier_id, vehicle_type)
);

-- 3. Tabla intermedia de clientes preferentes (Chips N:M de la UI)
CREATE TABLE wms.carrier_preferred_clients (
    carrier_id   UUID        NOT NULL REFERENCES wms.carriers(id) ON DELETE CASCADE,
    client_id    UUID        NOT NULL REFERENCES wms.clients(id) ON DELETE CASCADE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (carrier_id, client_id)
);

-- 4. Tabla wms.carrier_vehicle_metadata (Auditoría operativa en rampa)
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

-- Trigger de updated_at para wms.carrier_vehicle_metadata
CREATE TRIGGER trg_update_carrier_vehicle_metadata
    BEFORE UPDATE ON wms.carrier_vehicle_metadata
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- 5. Tabla wms.vehicle_seals (Sellos físicos 1:N)
CREATE TABLE wms.vehicle_seals (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    metadata_id     UUID        NOT NULL REFERENCES wms.carrier_vehicle_metadata(id) ON DELETE CASCADE,
    seal_number     VARCHAR(50) NOT NULL,
    seal_type       VARCHAR(30) DEFAULT 'STANDARD',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),

    CONSTRAINT chk_seal_number CHECK (char_length(seal_number) >= 3)
);

-- 6. Índices para mejorar rendimiento
CREATE INDEX idx_carriers_org ON wms.carriers (organization_id);
CREATE INDEX idx_cvt_carrier ON wms.carrier_vehicle_types (carrier_id);
CREATE INDEX idx_cpc_carrier ON wms.carrier_preferred_clients (carrier_id);
CREATE INDEX idx_cvm_item_id ON wms.carrier_vehicle_metadata (item_id);
CREATE INDEX idx_seals_metadata_id ON wms.vehicle_seals (metadata_id);

-- 7. Registrar permisos en wms.permissions
INSERT INTO wms.permissions (id, name, description) VALUES
    (uuid_generate_v4(), 'CARRIERS_READ',   'Permite leer el catálogo de transportistas'),
    (uuid_generate_v4(), 'CARRIERS_CREATE', 'Permite crear transportistas en el catálogo'),
    (uuid_generate_v4(), 'CARRIERS_UPDATE', 'Permite actualizar transportistas del catálogo'),
    (uuid_generate_v4(), 'CARRIERS_DELETE', 'Permite eliminar transportistas del catálogo')
ON CONFLICT (name) DO NOTHING;

-- 8. Mapear permisos a roles existentes
-- OPERATIONS_MANAGER: CRUD completo
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_MANAGER'
  AND p.name IN ('CARRIERS_READ','CARRIERS_CREATE','CARRIERS_UPDATE','CARRIERS_DELETE')
ON CONFLICT DO NOTHING;

-- CEO: Lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'CARRIERS_READ'
WHERE r.name = 'CEO' ON CONFLICT DO NOTHING;

-- OPERATIONS_SUPERVISOR: Lectura + Actualización
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p
    ON p.name IN ('CARRIERS_READ', 'CARRIERS_UPDATE')
WHERE r.name = 'OPERATIONS_SUPERVISOR' ON CONFLICT DO NOTHING;

-- CONTROL_DESK: Lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'CARRIERS_READ'
WHERE r.name = 'CONTROL_DESK' ON CONFLICT DO NOTHING;

-- SHIFT_LEADER: Lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'CARRIERS_READ'
WHERE r.name = 'SHIFT_LEADER' ON CONFLICT DO NOTHING;

-- =============================================================================
-- SEMILLA DE DATOS DE DEMO (UI MOCKUP INTEGRATION)
-- =============================================================================

-- Sembrar Clientes requeridos por el mockup de la UI
INSERT INTO wms.clients (id, organization_id, name, external_id, status) VALUES
    ('c73f0907-9fa5-4bdf-87db-2eb5e7683940', 'a53f0907-9fa5-4bdf-87db-2eb5e7683935', 'Lala S.A.', 'LALA-001', 'ACTIVE'),
    ('c73f0907-9fa5-4bdf-87db-2eb5e7683941', 'a53f0907-9fa5-4bdf-87db-2eb5e7683935', 'FEMSA Distribución', 'FEMSA-001', 'ACTIVE'),
    ('c73f0907-9fa5-4bdf-87db-2eb5e7683942', 'a53f0907-9fa5-4bdf-87db-2eb5e7683935', 'Grupo Bimbo', 'BIMBO-001', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Sembrar Transportista Demo
INSERT INTO wms.carriers (id, organization_id, name, trade_name, tax_id, carrier_type, status, contact_name, contact_phone, contact_email, service_type, permit_number, geographic_coverage, notes) VALUES
    (
        'a13f0907-9fa5-4bdf-87db-2eb5e7683950', 
        'a53f0907-9fa5-4bdf-87db-2eb5e7683935', 
        'Transportes del Noreste S.A. de C.V.', 
        'TransNoreste', 
        'TN0890314AB2', 
        'EXTERNAL', 
        'ACTIVE', 
        'Roberto Garza Hernández', 
        '8181234567', 
        'rgarza@transnoreste.com.mx', 
        'FTL', 
        'SCT-NL-00234-2022', 
        'Noreste, Centro y Bajío (NL, CDMX, QRO, GTO)', 
        'Transportista preferencial para rutas de alto volumen. Contrato vigente hasta 2027.'
    )
ON CONFLICT (id) DO NOTHING;

-- Sembrar Capacidades del Transportista
INSERT INTO wms.carrier_vehicle_types (carrier_id, vehicle_type) VALUES
    ('a13f0907-9fa5-4bdf-87db-2eb5e7683950', 'CAJA_SECA'),
    ('a13f0907-9fa5-4bdf-87db-2eb5e7683950', 'PLATAFORMA'),
    ('a13f0907-9fa5-4bdf-87db-2eb5e7683950', 'TRACTOCAMION')
ON CONFLICT DO NOTHING;

-- Sembrar Relación con Clientes Preferentes
INSERT INTO wms.carrier_preferred_clients (carrier_id, client_id) VALUES
    ('a13f0907-9fa5-4bdf-87db-2eb5e7683950', 'c73f0907-9fa5-4bdf-87db-2eb5e7683940'), -- Lala
    ('a13f0907-9fa5-4bdf-87db-2eb5e7683950', 'c73f0907-9fa5-4bdf-87db-2eb5e7683941'), -- FEMSA
    ('a13f0907-9fa5-4bdf-87db-2eb5e7683950', 'c73f0907-9fa5-4bdf-87db-2eb5e7683942')  -- Bimbo
ON CONFLICT DO NOTHING;
