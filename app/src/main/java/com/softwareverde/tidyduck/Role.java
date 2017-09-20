package com.softwareverde.tidyduck;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Role {

    private String _name;
    private Set<Permission> _permissions = new HashSet<>();

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    public Collection<Permission> getPermissions() {
        return new HashSet<>(_permissions);
    }

    /**
     * <p>Adds the permission.  Will not result in duplicates if a permission is already present.</p>
     * @param permission
     */
    public void addPermission(final Permission permission) {
        _permissions.add(permission);
    }

    public void setPermissions(final Collection<Permission> permissions) {
        _permissions = new HashSet<>(permissions);
    }
}
