-- =============================================================================
-- V2: Initial Reference and Test Data Seed
-- Author: 4GUARD Engineering Team (Senior Developer)
-- Description: Seeds the database with a default organization, default branch,
--              7 distinct roles (levels 1-7), 54 WMS permissions, mapping,
--              and the initial administrator user 'enrique'.
-- =============================================================================

SET search_path TO wms, public;

-- 1. Default Organization (4GUARD HQ)
INSERT INTO wms.organizations (id, name, code, tax_id, type, status, settings)
VALUES (
    'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
    '4GUARD LOGISTICS CORP',
    '4GUARD',
    'MX-99887766-A',
    'LOGISTICS',
    'ACTIVE',
    '{"theme": "dark", "notifications": {"email": true, "telegram": false}}'::jsonb
) ON CONFLICT (code) DO NOTHING;

-- 2. Default Branch (CDMX HQ)
INSERT INTO wms.branches (id, organization_id, name, code, timezone, address_line1, status)
VALUES (
    'b73f0907-9fa5-4bdf-87db-2eb5e7683936',
    'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
    'CENTRO DE DISTRIBUCION CDMX',
    'CDMX-01',
    'America/Mexico_City',
    'Av. Paseo de la Reforma 123, Ciudad de México',
    'ACTIVE'
) ON CONFLICT (organization_id, code) DO NOTHING;

-- 3. System Roles (Levels 1 to 7)
INSERT INTO wms.roles (id, name, level, is_system)
VALUES 
    ('88888888-8888-8888-8888-888888888888', 'OPERATIONS_MANAGER', 7, TRUE),
    ('66666666-6666-6666-6666-666666666666', 'CEO',                6, TRUE),
    ('55555555-5555-5555-5555-555555555555', 'OPERATIONS_SUPERVISOR', 5, TRUE),
    ('44444444-4444-4444-4444-444444444444', 'CONTROL_DESK',       4, TRUE),
    ('33333333-3333-3333-3333-333333333333', 'SHIFT_LEADER',        3, TRUE),
    ('22222222-2222-2222-2222-222222222222', 'WAREHOUSE_OPERATOR',  2, TRUE),
    ('11111111-1111-1111-1111-111111111111', 'MANEUVER_OPERATOR',   1, TRUE)
ON CONFLICT (name) DO NOTHING;

-- 4. Initial WMS Permissions (54 total permissions)
INSERT INTO wms.permissions (id, name, description)
VALUES 
    -- 1. inventory (5)
    (uuid_generate_v4(), 'INVENTORY_READ', 'Permite leer inventario'),
    (uuid_generate_v4(), 'INVENTORY_CREATE', 'Permite crear items de inventario'),
    (uuid_generate_v4(), 'INVENTORY_UPDATE', 'Permite actualizar items de inventario'),
    (uuid_generate_v4(), 'INVENTORY_DELETE', 'Permite borrar items de inventario'),
    (uuid_generate_v4(), 'INVENTORY_CONFIRM', 'Permite confirmar movimientos de inventario'),
    
    -- 2. receiving (4)
    (uuid_generate_v4(), 'RECEIVING_READ', 'Permite leer recepciones'),
    (uuid_generate_v4(), 'RECEIVING_CREATE', 'Permite crear recepciones'),
    (uuid_generate_v4(), 'RECEIVING_UPDATE', 'Permite actualizar recepciones'),
    (uuid_generate_v4(), 'RECEIVING_CONFIRM', 'Permite confirmar recepciones'),
    
    -- 3. picking (4)
    (uuid_generate_v4(), 'PICKING_READ', 'Permite leer órdenes de picking'),
    (uuid_generate_v4(), 'PICKING_CREATE', 'Permite crear órdenes de picking'),
    (uuid_generate_v4(), 'PICKING_UPDATE', 'Permite actualizar órdenes de picking'),
    (uuid_generate_v4(), 'PICKING_CONFIRM', 'Permite confirmar picking'),
    
    -- 4. packing (4)
    (uuid_generate_v4(), 'PACKING_READ', 'Permite leer empaque'),
    (uuid_generate_v4(), 'PACKING_CREATE', 'Permite crear empaque'),
    (uuid_generate_v4(), 'PACKING_UPDATE', 'Permite actualizar empaque'),
    (uuid_generate_v4(), 'PACKING_CONFIRM', 'Permite confirmar empaque'),
    
    -- 5. shipping (4)
    (uuid_generate_v4(), 'SHIPPING_READ', 'Permite leer embarques'),
    (uuid_generate_v4(), 'SHIPPING_CREATE', 'Permite crear embarques'),
    (uuid_generate_v4(), 'SHIPPING_UPDATE', 'Permite actualizar embarques'),
    (uuid_generate_v4(), 'SHIPPING_CONFIRM', 'Permite confirmar embarques'),
    
    -- 6. users (4)
    (uuid_generate_v4(), 'USERS_READ', 'Permite leer usuarios'),
    (uuid_generate_v4(), 'USERS_CREATE', 'Permite crear usuarios'),
    (uuid_generate_v4(), 'USERS_UPDATE', 'Permite actualizar usuarios'),
    (uuid_generate_v4(), 'USERS_DELETE', 'Permite borrar usuarios'),
    
    -- 7. clients (4)
    (uuid_generate_v4(), 'CLIENTS_READ', 'Permite leer clientes'),
    (uuid_generate_v4(), 'CLIENTS_CREATE', 'Permite crear clientes'),
    (uuid_generate_v4(), 'CLIENTS_UPDATE', 'Permite actualizar clientes'),
    (uuid_generate_v4(), 'CLIENTS_DELETE', 'Permite borrar clientes'),
    
    -- 8. dashboard (2)
    (uuid_generate_v4(), 'DASHBOARD_READ', 'Permite ver el dashboard'),
    (uuid_generate_v4(), 'DASHBOARD_EXECUTE', 'Permite ejecutar consultas de dashboard'),
    
    -- 9. layout (3)
    (uuid_generate_v4(), 'LAYOUT_READ', 'Permite leer el layout del almacén'),
    (uuid_generate_v4(), 'LAYOUT_UPDATE', 'Permite actualizar el layout del almacén'),
    (uuid_generate_v4(), 'LAYOUT_EXECUTE', 'Permite calcular optimizaciones de layout'),
    
    -- 10. quality (4)
    (uuid_generate_v4(), 'QUALITY_READ', 'Permite leer inspecciones de calidad'),
    (uuid_generate_v4(), 'QUALITY_UPDATE', 'Permite actualizar inspecciones de calidad'),
    (uuid_generate_v4(), 'QUALITY_AUTHORIZE', 'Permite autorizar calidad'),
    (uuid_generate_v4(), 'QUALITY_CONFIRM', 'Permite confirmar calidad'),
    
    -- 11. operations (4)
    (uuid_generate_v4(), 'OPERATIONS_READ', 'Permite leer bitácoras de operaciones'),
    (uuid_generate_v4(), 'OPERATIONS_CREATE', 'Permite registrar operaciones'),
    (uuid_generate_v4(), 'OPERATIONS_UPDATE', 'Permite actualizar operaciones'),
    (uuid_generate_v4(), 'OPERATIONS_EXECUTE', 'Permite ejecutar procesos operativos'),
    
    -- 12. metadata (3)
    (uuid_generate_v4(), 'METADATA_READ', 'Permite leer metadatos de transporte'),
    (uuid_generate_v4(), 'METADATA_CREATE', 'Permite registrar metadatos de transporte'),
    (uuid_generate_v4(), 'METADATA_UPDATE', 'Permite actualizar metadatos de transporte'),
    
    -- 13. ramps (4)
    (uuid_generate_v4(), 'RAMPS_READ', 'Permite leer estado de rampas'),
    (uuid_generate_v4(), 'RAMPS_CREATE', 'Permite registrar rampas'),
    (uuid_generate_v4(), 'RAMPS_UPDATE', 'Permite actualizar rampas'),
    (uuid_generate_v4(), 'RAMPS_AUTHORIZE', 'Permite autorizar asignación de rampas'),
    
    -- 14. labels (3)
    (uuid_generate_v4(), 'LABELS_READ', 'Permite leer etiquetas y códigos'),
    (uuid_generate_v4(), 'LABELS_CREATE', 'Permite generar etiquetas de código de barras'),
    (uuid_generate_v4(), 'LABELS_EXECUTE', 'Permite imprimir etiquetas'),
    
    -- 15. reports (3)
    (uuid_generate_v4(), 'REPORTS_READ', 'Permite leer reportes y estadísticas'),
    (uuid_generate_v4(), 'REPORTS_CREATE', 'Permite diseñar nuevos reportes'),
    (uuid_generate_v4(), 'REPORTS_EXECUTE', 'Permite exportar y generar reportes'),
    
    -- 16. audit (3)
    (uuid_generate_v4(), 'AUDIT_READ', 'Permite leer bitácora de auditoría'),
    (uuid_generate_v4(), 'AUDIT_CREATE', 'Permite generar registros de auditoría'),
    (uuid_generate_v4(), 'AUDIT_EXECUTE', 'Permite purgar logs de auditoría')
ON CONFLICT (name) DO NOTHING;

-- 5. Role Permissions mapping

-- 5.1 OPERATIONS_MANAGER: Posee los 54 permisos totales del sistema
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_MANAGER'
ON CONFLICT DO NOTHING;

-- 5.2 CEO: Tiene 18 permisos, limitados estrictamente a lectura y exportación
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN (
    'INVENTORY_READ', 'RECEIVING_READ', 'PICKING_READ', 'PACKING_READ', 'SHIPPING_READ', 
    'USERS_READ', 'CLIENTS_READ', 'DASHBOARD_READ', 'LAYOUT_READ', 'QUALITY_READ', 
    'OPERATIONS_READ', 'METADATA_READ', 'RAMPS_READ', 'LABELS_READ', 'REPORTS_READ', 
    'AUDIT_READ', 'REPORTS_EXECUTE', 'DASHBOARD_EXECUTE'
)
WHERE r.name = 'CEO'
ON CONFLICT DO NOTHING;

-- 5.3 OPERATIONS_SUPERVISOR: Cuenta con 26 permisos operativos
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN (
    'INVENTORY_READ', 'RECEIVING_READ', 'PICKING_READ', 'PACKING_READ', 'SHIPPING_READ', 
    'USERS_READ', 'CLIENTS_READ', 'DASHBOARD_READ', 'LAYOUT_READ', 'QUALITY_READ', 
    'OPERATIONS_READ', 'METADATA_READ', 'RAMPS_READ', 'LABELS_READ', 'REPORTS_READ', 
    'AUDIT_READ', 'INVENTORY_UPDATE', 'RECEIVING_UPDATE', 'PICKING_UPDATE', 'PACKING_UPDATE', 
    'SHIPPING_UPDATE', 'OPERATIONS_UPDATE', 'RAMPS_UPDATE', 'METADATA_UPDATE', 'REPORTS_EXECUTE', 
    'AUDIT_CREATE'
)
WHERE r.name = 'OPERATIONS_SUPERVISOR'
ON CONFLICT DO NOTHING;

-- 5.4 CONTROL_DESK: Posee 19 permisos enfocados en documentación y entrada
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN (
    'INVENTORY_READ', 'RECEIVING_READ', 'SHIPPING_READ', 'METADATA_READ', 'RAMPS_READ', 
    'LAYOUT_READ', 'CLIENTS_READ', 'LABELS_READ', 'REPORTS_READ', 'OPERATIONS_READ',
    'METADATA_CREATE', 'METADATA_UPDATE', 'RECEIVING_CREATE', 'RECEIVING_UPDATE', 
    'SHIPPING_CREATE', 'SHIPPING_UPDATE', 'RAMPS_UPDATE', 'RAMPS_AUTHORIZE', 'RECEIVING_CONFIRM'
)
WHERE r.name = 'CONTROL_DESK'
ON CONFLICT DO NOTHING;

-- 5.5 SHIFT_LEADER: Tiene 24 permisos tácticos y de supervisión de piso
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN (
    'INVENTORY_READ', 'RECEIVING_READ', 'PICKING_READ', 'PACKING_READ', 'SHIPPING_READ', 
    'LAYOUT_READ', 'QUALITY_READ', 'OPERATIONS_READ', 'RAMPS_READ', 'LABELS_READ', 
    'REPORTS_READ', 'METADATA_READ', 'RAMPS_AUTHORIZE', 'RAMPS_UPDATE', 'LAYOUT_UPDATE', 
    'LAYOUT_EXECUTE', 'INVENTORY_UPDATE', 'RECEIVING_CONFIRM', 'PICKING_CONFIRM', 'PACKING_CONFIRM', 
    'SHIPPING_CONFIRM', 'QUALITY_CONFIRM', 'LABELS_CREATE', 'LABELS_EXECUTE'
)
WHERE r.name = 'SHIFT_LEADER'
ON CONFLICT DO NOTHING;

-- 5.6 WAREHOUSE_OPERATOR: Cuenta con 17 permisos de ejecución física
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN (
    'INVENTORY_READ', 'RECEIVING_READ', 'PICKING_READ', 'PACKING_READ', 'SHIPPING_READ', 
    'LAYOUT_READ', 'LABELS_READ', 'INVENTORY_UPDATE', 'INVENTORY_CONFIRM', 'RECEIVING_UPDATE', 
    'RECEIVING_CONFIRM', 'PICKING_UPDATE', 'PICKING_CONFIRM', 'PACKING_UPDATE', 'PACKING_CONFIRM', 
    'SHIPPING_CONFIRM', 'LABELS_EXECUTE'
)
WHERE r.name = 'WAREHOUSE_OPERATOR'
ON CONFLICT DO NOTHING;

-- 5.7 MANEUVER_OPERATOR: Posee 14 permisos básicos para el uso de terminales móviles
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN (
    'INVENTORY_READ', 'RECEIVING_READ', 'PICKING_READ', 'LAYOUT_READ', 'LABELS_READ', 
    'METADATA_READ', 'PACKING_READ', 'SHIPPING_READ', 'INVENTORY_CONFIRM', 'RECEIVING_CONFIRM', 
    'PICKING_CONFIRM', 'LABELS_EXECUTE', 'PACKING_CONFIRM', 'SHIPPING_CONFIRM'
)
WHERE r.name = 'MANEUVER_OPERATOR'
ON CONFLICT DO NOTHING;

-- 6. Default Administrator User: enrique / admin123
-- Password hash generated using BCrypt strength 12: 'admin123'
-- Hash: '$2a$12$C.In8jGhHR4dRJQpkyIWoeN5bLIeLh7S7rZ9azVdP26ssfuOR6Hw.'
INSERT INTO wms.users (id, username, email, password, first_name, last_name, organization_id, branch_id, role_id, status, is_enabled)
VALUES (
    'f33f0907-9fa5-4bdf-87db-2eb5e7683937',
    'enrique',
    'enrique@4guard.com',
    '$2a$12$C.In8jGhHR4dRJQpkyIWoeN5bLIeLh7S7rZ9azVdP26ssfuOR6Hw.',
    'Enrique',
    'Architect',
    'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
    'b73f0907-9fa5-4bdf-87db-2eb5e7683936',
    '88888888-8888-8888-8888-888888888888',
    'ACTIVE',
    TRUE
) ON CONFLICT (username) DO NOTHING;

-- Test Users: Chris4G and Romel4G
INSERT INTO wms.users (id, username, email, password, first_name, last_name, organization_id, branch_id, role_id, status, is_enabled)
VALUES (
    'afe4de7c-d10e-44b9-8970-46a0fda50626',
    'Chris4G',
    'christian@4guard.mx',
    '$2a$12$clSxIMQpelhyCewjyiKAt.iyyBgViyojwf.fIzaHGfrjFy26duUFG',
    'Christian',
    'Duran Garcia',
    'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
    'b73f0907-9fa5-4bdf-87db-2eb5e7683936',
    '88888888-8888-8888-8888-888888888888',
    'ACTIVE',
    TRUE
) ON CONFLICT (username) DO NOTHING;

INSERT INTO wms.users (id, username, email, password, first_name, last_name, organization_id, branch_id, role_id, status, is_enabled)
VALUES (
    'fb31fe4c-bc27-4b1c-8846-7288812f84bf',
    'Romel4G',
    'romel@4guard.mx',
    '$2a$12$R8b0a8RAUEV5MLOsAMYFGO9ztDab5sXaTSGyJfY68SQxJTEDXzuqC',
    'Romel',
    'Salgado',
    'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
    'b73f0907-9fa5-4bdf-87db-2eb5e7683936',
    '88888888-8888-8888-8888-888888888888',
    'ACTIVE',
    TRUE
) ON CONFLICT (username) DO NOTHING;




