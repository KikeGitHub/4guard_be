-- =============================================================================
-- V2: Initial Reference and Test Data Seed
-- Author: 4GUARD Engineering Team (Senior Developer)
-- Description: Seeds the database with a default organization, default roles,
--              initial permissions, a default branch, and a default user
--              named 'enrique' with administrator access.
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

-- 2. Initial Permissions
INSERT INTO wms.permissions (id, name, description)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'AUTH_LOGIN', 'Permite iniciar sesión en el sistema'),
    ('22222222-2222-2222-2222-222222222222', 'WMS_READ',   'Permite leer información de inventario y topology'),
    ('33333333-3333-3333-3333-333333333333', 'WMS_WRITE',  'Permite realizar movimientos y crear SKUs'),
    ('44444444-4444-4444-4444-444444444444', 'SYS_ADMIN',  'Acceso total de administración al backend')
ON CONFLICT (name) DO NOTHING;

-- 3. System Roles (Level 7: ADMIN, Level 4: OPERATOR, Level 1: READONLY)
INSERT INTO wms.roles (id, name, level, is_system)
VALUES 
    ('88888888-8888-8888-8888-888888888888', 'ADMIN', 7, TRUE),
    ('77777777-7777-7777-7777-777777777777', 'OPERATOR', 4, TRUE)
ON CONFLICT (name) DO NOTHING;

-- 4. Role Permissions mapping
-- ADMIN gets all permissions
INSERT INTO wms.role_permissions (role_id, permission_id)
VALUES 
    ('88888888-8888-8888-8888-888888888888', '11111111-1111-1111-1111-111111111111'),
    ('88888888-8888-8888-8888-888888888888', '22222222-2222-2222-2222-222222222222'),
    ('88888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333'),
    ('88888888-8888-8888-8888-888888888888', '44444444-4444-4444-4444-444444444444')
ON CONFLICT DO NOTHING;

-- OPERATOR gets login, read, and write permissions (no administration access)
INSERT INTO wms.role_permissions (role_id, permission_id)
VALUES 
    ('77777777-7777-7777-7777-777777777777', '11111111-1111-1111-1111-111111111111'),
    ('77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222'),
    ('77777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333')
ON CONFLICT DO NOTHING;

-- 5. Default Branch (CDMX HQ)
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

-- 6. Default Administrator User: enrique / admin123
-- Password hash generated using BCrypt strength 12: 'admin123'
-- Hash: '$2a$12$041o7c92r0s0WvG3m0iUGeXjZl8Y7c1F14Z4O88C8w83p7383uO7q'
INSERT INTO wms.users (id, username, email, password, first_name, last_name, organization_id, branch_id, role_id, status, is_enabled)
VALUES (
    'f33f0907-9fa5-4bdf-87db-2eb5e7683937',
    'enrique',
    'enrique@4guard.com',
    '$2a$12$041o7c92r0s0WvG3m0iUGeXjZl8Y7c1F14Z4O88C8w83p7383uO7q',
    'Enrique',
    'Architect',
    'a53f0907-9fa5-4bdf-87db-2eb5e7683935',
    'b73f0907-9fa5-4bdf-87db-2eb5e7683936',
    '88888888-8888-8888-8888-888888888888',
    'ACTIVE',
    TRUE
) ON CONFLICT (username) DO NOTHING;
