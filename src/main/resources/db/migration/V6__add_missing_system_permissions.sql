-- =============================================================================
-- V6: Add Missing System Permissions
-- Description: Seeds the database with missing permissions for roles, permissions,
--              branches, organizations, sections, and locations, and maps them
--              to the appropriate system roles.
-- =============================================================================

SET search_path TO wms, public;

-- 1. Insert missing permissions
INSERT INTO wms.permissions (id, name, description)
VALUES 
    -- Roles (4)
    (uuid_generate_v4(), 'ROLES_READ', 'Permite leer roles'),
    (uuid_generate_v4(), 'ROLES_CREATE', 'Permite crear roles'),
    (uuid_generate_v4(), 'ROLES_UPDATE', 'Permite actualizar roles'),
    (uuid_generate_v4(), 'ROLES_DELETE', 'Permite borrar roles'),
    
    -- Permissions (3)
    (uuid_generate_v4(), 'PERMISSIONS_READ', 'Permite leer el catálogo de permisos'),
    (uuid_generate_v4(), 'PERMISSIONS_CREATE', 'Permite crear permisos'),
    (uuid_generate_v4(), 'PERMISSIONS_DELETE', 'Permite borrar permisos'),

    -- Branches (4)
    (uuid_generate_v4(), 'BRANCHES_READ', 'Permite leer sucursales'),
    (uuid_generate_v4(), 'BRANCHES_CREATE', 'Permite crear sucursales'),
    (uuid_generate_v4(), 'BRANCHES_UPDATE', 'Permite actualizar sucursales'),
    (uuid_generate_v4(), 'BRANCHES_DELETE', 'Permite borrar sucursales'),

    -- Organizations (4)
    (uuid_generate_v4(), 'ORGANIZATIONS_READ', 'Permite leer organizaciones'),
    (uuid_generate_v4(), 'ORGANIZATIONS_CREATE', 'Permite crear organizaciones'),
    (uuid_generate_v4(), 'ORGANIZATIONS_UPDATE', 'Permite actualizar organizaciones'),
    (uuid_generate_v4(), 'ORGANIZATIONS_DELETE', 'Permite borrar organizaciones'),

    -- Sections (4)
    (uuid_generate_v4(), 'SECTIONS_READ', 'Permite leer secciones del almacén'),
    (uuid_generate_v4(), 'SECTIONS_CREATE', 'Permite crear secciones del almacén'),
    (uuid_generate_v4(), 'SECTIONS_UPDATE', 'Permite actualizar secciones del almacén'),
    (uuid_generate_v4(), 'SECTIONS_DELETE', 'Permite borrar secciones del almacén'),

    -- Locations (4)
    (uuid_generate_v4(), 'LOCATIONS_READ', 'Permite leer ubicaciones del almacén'),
    (uuid_generate_v4(), 'LOCATIONS_CREATE', 'Permite crear ubicaciones del almacén'),
    (uuid_generate_v4(), 'LOCATIONS_UPDATE', 'Permite actualizar ubicaciones del almacén'),
    (uuid_generate_v4(), 'LOCATIONS_DELETE', 'Permite borrar ubicaciones del almacén')
ON CONFLICT (name) DO NOTHING;

-- 2. Assign all new permissions to OPERATIONS_MANAGER
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_MANAGER'
  AND p.name IN (
    'ROLES_READ', 'ROLES_CREATE', 'ROLES_UPDATE', 'ROLES_DELETE',
    'PERMISSIONS_READ', 'PERMISSIONS_CREATE', 'PERMISSIONS_DELETE',
    'BRANCHES_READ', 'BRANCHES_CREATE', 'BRANCHES_UPDATE', 'BRANCHES_DELETE',
    'ORGANIZATIONS_READ', 'ORGANIZATIONS_CREATE', 'ORGANIZATIONS_UPDATE', 'ORGANIZATIONS_DELETE',
    'SECTIONS_READ', 'SECTIONS_CREATE', 'SECTIONS_UPDATE', 'SECTIONS_DELETE',
    'LOCATIONS_READ', 'LOCATIONS_CREATE', 'LOCATIONS_UPDATE', 'LOCATIONS_DELETE'
  )
ON CONFLICT DO NOTHING;

-- 3. Assign read-only permissions to CEO
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
CROSS JOIN wms.permissions p
WHERE r.name = 'CEO'
  AND p.name IN (
    'ROLES_READ', 'PERMISSIONS_READ', 'BRANCHES_READ', 'ORGANIZATIONS_READ', 'SECTIONS_READ', 'LOCATIONS_READ'
  )
ON CONFLICT DO NOTHING;

-- 4. Assign read/write/update permissions to OPERATIONS_SUPERVISOR where appropriate
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_SUPERVISOR'
  AND p.name IN (
    'ROLES_READ', 'PERMISSIONS_READ', 'BRANCHES_READ', 'ORGANIZATIONS_READ',
    'SECTIONS_READ', 'SECTIONS_UPDATE',
    'LOCATIONS_READ', 'LOCATIONS_UPDATE'
  )
ON CONFLICT DO NOTHING;
