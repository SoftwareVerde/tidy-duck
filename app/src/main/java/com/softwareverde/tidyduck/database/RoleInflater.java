package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.Role;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class RoleInflater {

    private DatabaseConnection<Connection> _databaseConnection;

    public RoleInflater(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public Role inflateRole(final long roleId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM roles WHERE id = ?");
        query.setParameter(roleId);

        List<Row> rows = _databaseConnection.query(query);

        if (rows.size() == 0) {
            throw new IllegalArgumentException("Invalid role ID: " + roleId);
        }

        final Role role = _convertRowToRole(rows.get(0));
        return role;
    }

    public Role inflateRoleFromName(final String roleName) throws DatabaseException {
        final Query query = new Query("SELECT * FROM roles WHERE name = ?");
        query.setParameter(roleName);

        List<Row> rows = _databaseConnection.query(query);

        if (rows.size() == 0) {
            throw new IllegalArgumentException("Invalid role name: " + roleName);
        }

        final Role role = _convertRowToRole(rows.get(0));
        return role;
    }

    private Role _convertRowToRole(final Row row) throws DatabaseException {
        final long roleId = row.getLong("id");
        final String roleName = row.getString("name");

        final Role role = new Role();

        role.setId(roleId);
        role.setName(roleName);
        role.setPermissions(getPermissions(roleId));

        return role;
    }

    private List<Permission> getPermissions(final long roleId) throws DatabaseException {
        final Query query = new Query("SELECT role_permissions.* FROM role_permissions WHERE role_id = ?");
        query.setParameter(roleId);

        List<Row> rows = _databaseConnection.query(query);

        List<Permission> permissions = new ArrayList<>();
        for (final Row row : rows) {
            addPermission(permissions, Permission.LOGIN,                                row.getBoolean("login"));
            addPermission(permissions, Permission.ADMIN_CREATE_USERS,                   row.getBoolean("admin_create_users"));
            addPermission(permissions, Permission.ADMIN_MODIFY_USERS,                   row.getBoolean("admin_modify_users"));
            addPermission(permissions, Permission.ADMIN_DELETE_USERS,                   row.getBoolean("admin_delete_users"));
            addPermission(permissions, Permission.ADMIN_RESET_PASSWORD,                 row.getBoolean("admin_reset_password"));
            addPermission(permissions, Permission.ADMIN_MODIFY_APPLICATION_SETTINGS,    row.getBoolean("admin_modify_application_settings"));
            addPermission(permissions, Permission.MOST_COMPONENTS_RELEASE,              row.getBoolean("most_components_release"));
            addPermission(permissions, Permission.MOST_COMPONENTS_CREATE,               row.getBoolean("most_components_create"));
            addPermission(permissions, Permission.MOST_COMPONENTS_MODIFY,               row.getBoolean("most_components_modify"));
            addPermission(permissions, Permission.MOST_COMPONENTS_VIEW,                 row.getBoolean("most_components_view"));
            addPermission(permissions, Permission.TYPES_CREATE,                         row.getBoolean("types_create"));
            addPermission(permissions, Permission.TYPES_MODIFY,                         row.getBoolean("types_modify"));
            addPermission(permissions, Permission.REVIEWS_APPROVAL,                     row.getBoolean("reviews_approval"));
            addPermission(permissions, Permission.REVIEWS_COMMENTS,                     row.getBoolean("reviews_comments"));
            addPermission(permissions, Permission.REVIEWS_VOTING,                       row.getBoolean("reviews_voting"));
            addPermission(permissions, Permission.REVIEWS_VIEW,                         row.getBoolean("reviews_view"));
        }
        return permissions;
    }

    private void addPermission(final List<Permission> permissions, final Permission permission, final boolean shouldHavePermission) {
        if (shouldHavePermission) {
            permissions.add(permission);
        }
    }
}
