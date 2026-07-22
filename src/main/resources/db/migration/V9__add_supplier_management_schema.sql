-- =============================================================================
-- V9: HU-125 Catálogo Maestro de Proveedores (Supplier Management)
-- Author: 4GUARD Engineering Team
-- Description: Crea el modelo relacional completo para la gestión de proveedores
--              dentro del WMS. Incluye:
--                - Catálogos de soporte (tipos y monedas)
--                - Tabla maestra de proveedores con soporte 3PL (RLS por scope)
--                - Tablas relacionadas: contacto, dirección, condiciones comerciales
--                - Integración con wms.audit_logs y wms.audit_log_details existentes
--                - Permisos RBAC alineados al patrón de V7 (carriers)
--                - Datos de demo compatibles con el mockup del FE (HU-125)
-- =============================================================================

SET search_path TO wms, public;

-- =============================================================================
-- SECCIÓN 1: CATÁLOGOS DE SOPORTE
-- =============================================================================

-- 1.1 Catálogo de tipos de proveedor
-- Actualmente hardcoded en el FE (supplier.model.ts). Al moverlo a BD permite
-- agregar/modificar tipos sin redeploy. La columna is_service determina si el
-- campo "lead_time_days" se interpreta como "tiempo de entrega" (bienes)
-- o "tiempo de respuesta" (servicios) — lógica reflejada en el FE.
-- -----------------------------------------------------------------------------
CREATE TABLE wms.cat_supplier_types (
    code        VARCHAR(30)     PRIMARY KEY,           -- 'PACKAGING', 'PALLETS', etc.
    label_es    VARCHAR(100)    NOT NULL,
    label_en    VARCHAR(100),
    is_service  BOOLEAN         NOT NULL DEFAULT FALSE, -- FALSE = bienes, TRUE = servicios
    sort_order  SMALLINT        NOT NULL DEFAULT 0,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

INSERT INTO wms.cat_supplier_types (code, label_es, label_en, is_service, sort_order) VALUES
    ('GOODS',            'Bienes y mercancías',                  'Goods',                    FALSE, 1),
    ('RAW_MATERIAL',     'Materia prima',                        'Raw Material',             FALSE, 2),
    ('PACKAGING',        'Material de empaque',                  'Packaging',                FALSE, 3),
    ('PALLETS',          'Tarimas y estibas',                    'Pallets',                  FALSE, 4),
    ('SPARE_PARTS',      'Refacciones',                          'Spare Parts',              FALSE, 5),
    ('TRANSPORT',        'Servicios de transporte (comercial)',   'Transport (commercial)',   TRUE,  6),
    ('MAINTENANCE',      'Mantenimiento',                        'Maintenance',              TRUE,  7),
    ('CLEANING',         'Limpieza industrial',                  'Cleaning',                 TRUE,  8),
    ('SECURITY',         'Seguridad y vigilancia',               'Security',                 TRUE,  9),
    ('PEST_CONTROL',     'Control de plagas / Fumigación',       'Pest Control',             TRUE,  10),
    ('TECHNOLOGY',       'Tecnología y sistemas',                'Technology',               TRUE,  11),
    ('GENERAL_SERVICES', 'Servicios generales',                  'General Services',         TRUE,  12),
    ('OTHER',            'Otros suministros',                    'Other',                    FALSE, 13);

-- 1.2 Catálogo de monedas
-- Usado en wms.supplier_commercial_terms.currency_code
-- -----------------------------------------------------------------------------
CREATE TABLE wms.cat_currencies (
    code    CHAR(3)     PRIMARY KEY,     -- ISO 4217: 'MXN', 'USD', 'EUR'
    label   VARCHAR(60) NOT NULL,
    symbol  VARCHAR(5),
    active  BOOLEAN     NOT NULL DEFAULT TRUE
);

INSERT INTO wms.cat_currencies (code, label, symbol) VALUES
    ('MXN', 'Pesos Mexicanos',  '$'),
    ('USD', 'Dólares US',       'US$'),
    ('EUR', 'Euros',            '€');

-- =============================================================================
-- SECCIÓN 2: TABLA MAESTRA DE PROVEEDORES
-- =============================================================================

-- 2.1 suppliers — Catálogo maestro de proveedores
-- La columna scope_type define visibilidad en arquitectura 3PL:
--   GLOBAL    → visible para toda la organización (client_id = NULL, branch_id = NULL)
--   CLIENT    → visible solo para un cliente propietario de mercancía
--   WAREHOUSE → visible solo para una sede/almacén (usando wms.branches)
--
-- DECISIÓN DE DISEÑO (dirección):
--   Se normaliza en tabla separada wms.supplier_addresses para escalar a
--   múltiples direcciones futuras (fiscal, operativa, sucursales) sin alterar
--   esta tabla. Es la misma estrategia que carriers usa con datos planos.
-- -----------------------------------------------------------------------------
CREATE TABLE wms.suppliers (
    -- Identidad
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id     UUID            NOT NULL REFERENCES wms.organizations(id),
    code                VARCHAR(20)     NOT NULL,  -- 'PRV-0001', secuencial por organización

    -- Información fiscal
    legal_name          VARCHAR(250)    NOT NULL,
    commercial_name     VARCHAR(150),
    tax_id              VARCHAR(20)     NOT NULL,  -- RFC 12 (moral) / 13 (física) / Tax ID extranjero

    -- Clasificación
    supplier_type_code  VARCHAR(30)     NOT NULL REFERENCES wms.cat_supplier_types(code),
    is_preferred        BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Estado operativo
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    status_reason       VARCHAR(500),              -- Obligatorio si status IN ('INACTIVE','BLOCKED')
    status_changed_at   TIMESTAMPTZ,
    status_changed_by   VARCHAR(100),              -- username del operador

    -- Alcance 3PL (RLS)
    scope_type          VARCHAR(15)     NOT NULL DEFAULT 'GLOBAL',
    client_id           UUID            REFERENCES wms.clients(id),    -- Requerido si scope=CLIENT
    branch_id           UUID            REFERENCES wms.branches(id),   -- Requerido si scope=WAREHOUSE

    -- Notas operativas
    notes               TEXT,

    -- Borrado lógico (nunca se elimina físicamente)
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),

    -- Control
    version             BIGINT          NOT NULL DEFAULT 1,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100)    NOT NULL DEFAULT 'system',
    updated_by          VARCHAR(100)    NOT NULL DEFAULT 'system',

    -- Constraints
    UNIQUE (organization_id, code),
    UNIQUE (organization_id, tax_id),                                -- RFC único por organización
    CONSTRAINT chk_supplier_status
        CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED')),
    CONSTRAINT chk_supplier_scope
        CHECK (scope_type IN ('GLOBAL','CLIENT','WAREHOUSE')),
    CONSTRAINT chk_scope_client
        CHECK (scope_type != 'CLIENT'    OR client_id IS NOT NULL),
    CONSTRAINT chk_scope_warehouse
        CHECK (scope_type != 'WAREHOUSE' OR branch_id IS NOT NULL),
    CONSTRAINT chk_legal_name_min
        CHECK (char_length(legal_name) >= 3),
    CONSTRAINT chk_tax_id_min
        CHECK (char_length(tax_id) >= 3)
);

-- Trigger de updated_at + version (igual que carriers, inventory_items, etc.)
CREATE TRIGGER trg_update_suppliers
    BEFORE UPDATE ON wms.suppliers
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- Índices para búsquedas del buscador del FE y filtros del sidebar
CREATE INDEX idx_suppliers_org         ON wms.suppliers (organization_id, is_deleted);
CREATE INDEX idx_suppliers_status      ON wms.suppliers (status, is_deleted);
CREATE INDEX idx_suppliers_type        ON wms.suppliers (supplier_type_code);
CREATE INDEX idx_suppliers_scope       ON wms.suppliers (scope_type, organization_id);
CREATE INDEX idx_suppliers_legal_name  ON wms.suppliers (lower(legal_name));
CREATE INDEX idx_suppliers_tax_id      ON wms.suppliers (tax_id);
CREATE INDEX idx_suppliers_preferred   ON wms.suppliers (is_preferred, is_deleted);

-- =============================================================================
-- SECCIÓN 3: TABLAS RELACIONADAS (1:1)
-- =============================================================================

-- 3.1 supplier_contacts — Contacto principal del proveedor
-- Diseñado 1:1 actualmente (restricción UNIQUE en supplier_id).
-- Para escalar a N contactos: eliminar la UNIQUE constraint y agregar
-- la columna contact_type VARCHAR(30) ('PRIMARY','BILLING','OPERATIONS', etc.)
-- -----------------------------------------------------------------------------
CREATE TABLE wms.supplier_contacts (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    supplier_id     UUID            NOT NULL UNIQUE REFERENCES wms.suppliers(id) ON DELETE CASCADE,
    full_name       VARCHAR(150)    NOT NULL,
    job_title       VARCHAR(100),
    email           VARCHAR(150)    NOT NULL,
    phone           VARCHAR(25)     NOT NULL,
    alt_phone       VARCHAR(25),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_contact_email_min CHECK (char_length(email) >= 6),
    CONSTRAINT chk_contact_phone_min CHECK (char_length(phone) >= 7)
);

CREATE TRIGGER trg_update_supplier_contacts
    BEFORE UPDATE ON wms.supplier_contacts
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- 3.2 supplier_addresses — Dirección fiscal / operativa
-- Tabla separada para escalar a múltiples direcciones en el futuro
-- sin alterar wms.suppliers.
-- -----------------------------------------------------------------------------
CREATE TABLE wms.supplier_addresses (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    supplier_id         UUID            NOT NULL UNIQUE REFERENCES wms.suppliers(id) ON DELETE CASCADE,
    country             VARCHAR(80)     NOT NULL DEFAULT 'México',
    state               VARCHAR(80)     NOT NULL,
    municipality        VARCHAR(80),
    city                VARCHAR(80)     NOT NULL,
    postal_code         VARCHAR(10),
    street              VARCHAR(200),
    exterior_number     VARCHAR(20),
    interior_number     VARCHAR(20),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_update_supplier_addresses
    BEFORE UPDATE ON wms.supplier_addresses
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- 3.3 supplier_commercial_terms — Condiciones operativas y comerciales
-- lead_time_days: "Tiempo de entrega" para bienes / "Tiempo de respuesta" para servicios
-- (la distinción la hace cat_supplier_types.is_service — lógica en capa de servicio)
-- -----------------------------------------------------------------------------
CREATE TABLE wms.supplier_commercial_terms (
    id                              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    supplier_id                     UUID            NOT NULL UNIQUE REFERENCES wms.suppliers(id) ON DELETE CASCADE,
    lead_time_days                  SMALLINT        NOT NULL DEFAULT 0,
    minimum_order_amount            NUMERIC(14,2)   NOT NULL DEFAULT 0,
    credit_days                     SMALLINT        NOT NULL DEFAULT 0,
    currency_code                   CHAR(3)         NOT NULL DEFAULT 'MXN' REFERENCES wms.cat_currencies(code),
    quality_inspection_required     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_lead_time_positive    CHECK (lead_time_days        >= 0),
    CONSTRAINT chk_min_order_positive    CHECK (minimum_order_amount  >= 0),
    CONSTRAINT chk_credit_days_positive  CHECK (credit_days           >= 0)
);

CREATE TRIGGER trg_update_supplier_commercial_terms
    BEFORE UPDATE ON wms.supplier_commercial_terms
    FOR EACH ROW EXECUTE FUNCTION wms.update_updated_at_column();

-- =============================================================================
-- SECCIÓN 4: SECUENCIA PARA CÓDIGO DE PROVEEDOR (por organización)
-- =============================================================================
-- La generación del código 'PRV-0001' es responsabilidad del BE.
-- Se usa una secuencia global + prefijo por organización (o consulta MAX).
-- La capa de servicio ejecutará:
--   SELECT COALESCE(MAX(CAST(SUBSTRING(code, 5) AS INTEGER)), 0) + 1
--   FROM wms.suppliers WHERE organization_id = ? AND NOT is_deleted
-- y formateará como 'PRV-' || LPAD(next_val::text, 4, '0')
-- No se crea secuencia de Postgres aquí porque es por organización, no global.

-- =============================================================================
-- SECCIÓN 5: PERMISOS RBAC
-- Alineados al patrón de V7 (carriers)
-- =============================================================================

INSERT INTO wms.permissions (id, name, description) VALUES
    (uuid_generate_v4(), 'SUPPLIERS_READ',          'Permite leer el catálogo de proveedores'),
    (uuid_generate_v4(), 'SUPPLIERS_CREATE',         'Permite crear proveedores en el catálogo'),
    (uuid_generate_v4(), 'SUPPLIERS_UPDATE',         'Permite actualizar proveedores del catálogo'),
    (uuid_generate_v4(), 'SUPPLIERS_DELETE',         'Permite archivar proveedores del catálogo'),
    (uuid_generate_v4(), 'SUPPLIERS_STATUS_CHANGE',  'Permite cambiar el estado operativo de un proveedor')
ON CONFLICT (name) DO NOTHING;

-- OPERATIONS_MANAGER → CRUD completo + cambio de estado
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_MANAGER'
  AND p.name IN ('SUPPLIERS_READ','SUPPLIERS_CREATE','SUPPLIERS_UPDATE','SUPPLIERS_DELETE','SUPPLIERS_STATUS_CHANGE')
ON CONFLICT DO NOTHING;

-- CEO → Solo lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'SUPPLIERS_READ'
WHERE r.name = 'CEO' ON CONFLICT DO NOTHING;

-- OPERATIONS_SUPERVISOR → Lectura + Actualización + Cambio de estado
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p
    ON p.name IN ('SUPPLIERS_READ','SUPPLIERS_UPDATE','SUPPLIERS_STATUS_CHANGE')
WHERE r.name = 'OPERATIONS_SUPERVISOR' ON CONFLICT DO NOTHING;

-- CONTROL_DESK → Solo lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'SUPPLIERS_READ'
WHERE r.name = 'CONTROL_DESK' ON CONFLICT DO NOTHING;

-- SHIFT_LEADER → Solo lectura
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM wms.roles r JOIN wms.permissions p ON p.name = 'SUPPLIERS_READ'
WHERE r.name = 'SHIFT_LEADER' ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÓN 6: DATOS DE DEMO
-- Compatibles con los mocks de supplier.service.ts (HU-125)
-- Se usan los clientes y branches ya sembrados en V7
-- =============================================================================

-- Demo org y branch existentes del schema inicial
-- organization_id = 'a53f0907-9fa5-4bdf-87db-2eb5e7683935'
-- branch_id       = (usar el primer branch de esa org)

-- 6.1 Proveedor 1 — EmpaquesNorte (PACKAGING, GLOBAL, ACTIVO, PREFERENTE)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, scope_type, notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000001',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0001',
     'Empaques Nacionales del Norte S.A. de C.V.',
     'EmpaquesNorte',
     'ENN980415HG8',
     'PACKAGING',
     TRUE, 'ACTIVE', 'GLOBAL',
     'Proveedor preferente para cajas de cartón corrugado y esquineros. Contrato anual renovado.',
     'admin', 'jperez')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone, alt_phone) VALUES
    ('b1000001-0000-0000-0000-000000000001',
     'Carlos Eduardo Mendoza', 'Gerente de Cuentas Clave',
     'cmendoza@empaquesnorte.com.mx', '8183456789', '8181239900')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, municipality, city, postal_code, street, exterior_number) VALUES
    ('b1000001-0000-0000-0000-000000000001',
     'México', 'Nuevo León', 'Apodaca', 'Monterrey', '66600',
     'Av. Industrias Alimentarias', '450')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000001', 3, 15000.00, 30, 'MXN', TRUE)
ON CONFLICT (supplier_id) DO NOTHING;

-- 6.2 Proveedor 2 — Tarimas del Centro (PALLETS, WAREHOUSE, ACTIVO, PREFERENTE)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, scope_type,
     branch_id,
     notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000002',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0002',
     'Tarimas y Tarimas del Centro S. de R.L.',
     'Tarimas del Centro',
     'TTC051120AB4',
     'PALLETS',
     TRUE, 'ACTIVE', 'WAREHOUSE',
     (SELECT id FROM wms.branches WHERE organization_id = 'a53f0907-9fa5-4bdf-87db-2eb5e7683935' LIMIT 1),
     'Suministro exclusivo de tarimas CHEP y taconas tratadas con norma HT / NOM-144-SEMARNAT.',
     'admin', 'supervisor01')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone) VALUES
    ('b1000001-0000-0000-0000-000000000002',
     'Gabriela Silva Paredes', 'Coordinadora de Ventas',
     'gsilva@tarimasdelcentro.com', '5557890123')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, city, postal_code, street, exterior_number) VALUES
    ('b1000001-0000-0000-0000-000000000002',
     'México', 'Estado de México', 'Toluca', '50070',
     'Vía José López Portillo', '1200')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000002', 2, 25000.00, 45, 'MXN', TRUE)
ON CONFLICT (supplier_id) DO NOTHING;

-- 6.3 Proveedor 3 — FumiToluca 3PL (PEST_CONTROL, WAREHOUSE, ACTIVO)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, scope_type,
     branch_id,
     notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000003',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0003',
     'Fumigaciones y Control Ambiental Toluca S.A.',
     'FumiToluca 3PL',
     'FCA120803KL9',
     'PEST_CONTROL',
     FALSE, 'ACTIVE', 'WAREHOUSE',
     (SELECT id FROM wms.branches WHERE organization_id = 'a53f0907-9fa5-4bdf-87db-2eb5e7683935' LIMIT 1),
     'Servicio mensual de fumigación y control integrado de plagas.',
     'jperez', 'jperez')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone) VALUES
    ('b1000001-0000-0000-0000-000000000003',
     'Ing. Rodrigo Alarcón', 'Director Operativo',
     'ralarcon@fumitoluca.mx', '7229876543')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, city, postal_code, street, exterior_number) VALUES
    ('b1000001-0000-0000-0000-000000000003',
     'México', 'Estado de México', 'Toluca', '50120',
     'Av. Tecnológico', '88')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000003', 1, 5000.00, 15, 'MXN', FALSE)
ON CONFLICT (supplier_id) DO NOTHING;

-- 6.4 Proveedor 4 — Montacargas del Valle (MAINTENANCE, CLIENT, BLOQUEADO)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, status_reason, status_changed_at, status_changed_by,
     scope_type, client_id,
     notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000004',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0004',
     'Montacargas y Mantenimiento del Valle S.A. de C.V.',
     'Montacargas del Valle',
     'MMV090214RT1',
     'MAINTENANCE',
     FALSE, 'BLOCKED',
     'Incumplimiento de tiempo de respuesta en reparación crítica de montacargas Reach.',
     NOW(), 'supervisor01',
     'CLIENT',
     'c73f0907-9fa5-4bdf-87db-2eb5e7683940',  -- Lala S.A. (sembrado en V7)
     'Bloqueado temporalmente hasta resolver revisión de SLA de mantenimiento preventivo.',
     'admin', 'supervisor01')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone) VALUES
    ('b1000001-0000-0000-0000-000000000004',
     'Lic. Fernando Gutiérrez', 'Jefe de Servicio Técnico',
     'fgutierrez@montacargasvalle.com', '5543210987')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, city, postal_code, street, exterior_number) VALUES
    ('b1000001-0000-0000-0000-000000000004',
     'México', 'Ciudad de México', 'Azcapotzalco', '02300',
     'Calzada Vallejo', '780')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000004', 2, 8000.00, 30, 'MXN', TRUE)
ON CONFLICT (supplier_id) DO NOTHING;

-- 6.5 Proveedor 5 — TecnoLogística MX (TECHNOLOGY, GLOBAL, ACTIVO, PREFERENTE, USD)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, scope_type, notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000005',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0005',
     'Tecnología y Sistemas Logísticos MX S.A. de C.V.',
     'TecnoLogística MX',
     'TSL160330PQ5',
     'TECHNOLOGY',
     TRUE, 'ACTIVE', 'GLOBAL',
     'Proveedor de licencias de colectores Zebra, impresoras térmicas e infraestructura IoT.',
     'admin', 'admin')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone) VALUES
    ('b1000001-0000-0000-0000-000000000005',
     'Dra. Sofía Hernández', 'Account Executive WMS/IoT',
     'shernandez@tecnologistica.mx', '5511223344')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, city, postal_code, street, exterior_number, interior_number) VALUES
    ('b1000001-0000-0000-0000-000000000005',
     'México', 'Ciudad de México', 'Miguel Hidalgo', '11560',
     'Av. Paseo de la Reforma', '222', 'Piso 8')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000005', 1, 1200.00, 30, 'USD', FALSE)
ON CONFLICT (supplier_id) DO NOTHING;

-- 6.6 Proveedor 6 — Limpieza 4G (CLEANING, WAREHOUSE, INACTIVO)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, status_reason, status_changed_at, status_changed_by,
     scope_type, branch_id,
     notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000006',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0006',
     'Limpieza Industrial y Servicios 4G S.A. de C.V.',
     'Limpieza 4G',
     'LIS190510UV2',
     'CLEANING',
     FALSE, 'INACTIVE',
     'Fin de contrato por licitación anual. Sustituido por proveedor local.',
     NOW(), 'admin',
     'WAREHOUSE',
     (SELECT id FROM wms.branches WHERE organization_id = 'a53f0907-9fa5-4bdf-87db-2eb5e7683935' LIMIT 1),
     'Inactivo. Se conserva el historial de facturación para auditoría fiscal.',
     'jperez', 'admin')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone) VALUES
    ('b1000001-0000-0000-0000-000000000006',
     'Patricia Morales', 'Coordinadora de Personal',
     'pmorales@limpieza4g.com', '7224433221')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, city, postal_code, street, exterior_number) VALUES
    ('b1000001-0000-0000-0000-000000000006',
     'México', 'Estado de México', 'Toluca', '50000',
     'Av. Morelos', '312')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000006', 1, 10000.00, 15, 'MXN', FALSE)
ON CONFLICT (supplier_id) DO NOTHING;

-- 6.7 Proveedor 7 — Seguridad Operativa (SECURITY, GLOBAL, ACTIVO)
INSERT INTO wms.suppliers
    (id, organization_id, code, legal_name, commercial_name, tax_id, supplier_type_code,
     is_preferred, status, scope_type, notes, created_by, updated_by)
VALUES
    ('b1000001-0000-0000-0000-000000000007',
     'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
     'PRV-0007',
     'Seguridad Operativa y Patrimonial del Centro S.A.',
     'Seguridad Operativa',
     'SOP140108ZA9',
     'SECURITY',
     FALSE, 'ACTIVE', 'GLOBAL',
     'Vigilancia 24/7 en casetas Smart Gate y patrullaje perimetral en almacenes.',
     'admin', 'supervisor01')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO wms.supplier_contacts (supplier_id, full_name, job_title, email, phone) VALUES
    ('b1000001-0000-0000-0000-000000000007',
     'Capitán Alberto Morales', 'Comandante de Zona',
     'amorales@seguridadoperativa.com.mx', '5566778899')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_addresses (supplier_id, country, state, city, postal_code, street, exterior_number) VALUES
    ('b1000001-0000-0000-0000-000000000007',
     'México', 'Ciudad de México', 'Cuauhtémoc', '06600',
     'Calle Insurgentes Sur', '105')
ON CONFLICT (supplier_id) DO NOTHING;

INSERT INTO wms.supplier_commercial_terms (supplier_id, lead_time_days, minimum_order_amount, credit_days, currency_code, quality_inspection_required) VALUES
    ('b1000001-0000-0000-0000-000000000007', 1, 35000.00, 30, 'MXN', TRUE)
ON CONFLICT (supplier_id) DO NOTHING;

-- =============================================================================
-- FIN V9 — HU-125 Catálogo Maestro de Proveedores
-- =============================================================================
-- NOTA PARA EL EQUIPO BE:
--
-- La auditoría de proveedores se escribe en las tablas ya existentes:
--   wms.audit_logs       → una fila por operación (CREATE/UPDATE/STATUS/ARCHIVE)
--   wms.audit_log_details → una fila por campo modificado (field_name, old_value, new_value)
--
-- entity_type a usar: 'SUPPLIER'
-- Acciones a usar: 'SUPPLIER_CREATED' | 'SUPPLIER_UPDATED' | 'SUPPLIER_STATUS_UPDATED' | 'SUPPLIER_ARCHIVED'
--
-- El FE consume el historial vía GET /api/suppliers/{id}/audit y espera la
-- estructura definida en CarrierAuditEntry (supplier.model.ts → SupplierAuditEntry).
-- =============================================================================
