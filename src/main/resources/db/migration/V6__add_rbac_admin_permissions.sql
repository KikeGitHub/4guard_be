-- =============================================================================
-- V6: Add RBAC Admin Permissions for Roles and Permissions management
-- Author: 4GUARD Engineering Team
-- Description: Inserts the 8 new permissions required by the Roles & Permissions
--              CRUD endpoints (RoleController + PermissionController).
--              Assigns ALL new permissions to the OPERATIONS_MANAGER role,
--              which uses a CROSS JOIN strategy (receives every permission).
-- =============================================================================

SET search_path TO wms, public;

-- 1. Insert new RBAC admin permissions
INSERT INTO wms.permissions (id, name, description)
VALUES
    -- Roles management (5 permissions)
    (uuid_generate_v4(), 'ROLES_READ',         'Permite ver el catálogo de roles del sistema'),
    (uuid_generate_v4(), 'ROLES_CREATE',        'Permite crear nuevos roles'),
    (uuid_generate_v4(), 'ROLES_UPDATE',        'Permite actualizar roles y asignar permisos'),
    (uuid_generate_v4(), 'ROLES_DELETE',        'Permite eliminar roles no asignados'),

    -- Permissions management (3 permissions)
    (uuid_generate_v4(), 'PERMISSIONS_READ',    'Permite ver el catálogo de permisos del sistema'),
    (uuid_generate_v4(), 'PERMISSIONS_CREATE',  'Permite agregar nuevos permisos al catálogo'),
    (uuid_generate_v4(), 'PERMISSIONS_DELETE',  'Permite eliminar permisos del catálogo')

ON CONFLICT (name) DO NOTHING;

-- 2. Assign ALL new permissions to OPERATIONS_MANAGER (re-uses the CROSS JOIN pattern)
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
CROSS JOIN wms.permissions p
WHERE r.name = 'OPERATIONS_MANAGER'
  AND p.name IN (
    'ROLES_READ', 'ROLES_CREATE', 'ROLES_UPDATE', 'ROLES_DELETE',
    'PERMISSIONS_READ', 'PERMISSIONS_CREATE', 'PERMISSIONS_DELETE'
  )
ON CONFLICT DO NOTHING;

-- 3. Assign ROLES_READ and PERMISSIONS_READ to CEO (read-only access to RBAC)
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN ('ROLES_READ', 'PERMISSIONS_READ')
WHERE r.name = 'CEO'
ON CONFLICT DO NOTHING;

-- 4. Assign ROLES_READ and PERMISSIONS_READ to OPERATIONS_SUPERVISOR
INSERT INTO wms.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM wms.roles r
JOIN wms.permissions p ON p.name IN ('ROLES_READ', 'PERMISSIONS_READ')
WHERE r.name = 'OPERATIONS_SUPERVISOR'
ON CONFLICT DO NOTHING;
