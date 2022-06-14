package com.softwareverde.tidyduck;

import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Account {
    private AccountId _id;
    private String _username;
    private String _password;
    private String _name;
    private Company _company;

    private Settings _settings;
    private Set<Role> _roles = new HashSet<>();

    public AccountId getId() {
        return _id;
    }

    public void setId(AccountId id) {
        this._id = id;
    }

    public void setUsername(final String username) {
        _username = username;
    }

    public String getUsername() {
        return _username;
    }

    public void setPassword(final String password) {
        _password = password;
    }

    public String getPassword() {
        return _password;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(Company company) {
        this._company = company;
    }

    public Settings getSettings() {
        return _settings;
    }

    public void setSettings(Settings settings) {
        this._settings = settings;
    }

    public Collection<Role> getRoles() {
        return new HashSet<>(_roles);
    }

    public void addRole(final Role role) {
        _roles.add(role);
    }

    public void setRoles(final Collection<Role> roles) {
        _roles = new HashSet<>(roles);
    }

    /**
     * <p>Returns all of the permissions the account has (i.e. those associated with each of its roles).</p>
     * @param permission
     * @return
     */
    public Collection<Permission> getPermissions(final Permission permission) {
        return _getAllRolePermissions();
    }

    private Collection<Permission> _getAllRolePermissions() {
        HashSet<Permission> permissions = new HashSet<>();
        for (final Role role : _roles) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }

    /**
     * <p>Returns true if and only if the user has the specified permission.</p>
     * @param permission
     * @return
     */
    public boolean hasPermission(final Permission permission) {
        return _getAllRolePermissions().contains(permission);
    }

    /**
     * <p>Throws an AuthorizationException if the account does not have the specified permission.</p>
     * @param permission
     * @throws AuthorizationException
     */
    public void requirePermission(final Permission permission) throws AuthorizationException {
        if (!_getAllRolePermissions().contains(permission)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("User ");
            stringBuilder.append(_username);
            stringBuilder.append(" does not have permission: ");
            stringBuilder.append(permission.name());

            throw new AuthorizationException(stringBuilder.toString());
        }
    }

    public Author toAuthor() {
        Author author = new Author();
        author.setId(_id);
        author.setName(_name);
        author.setCompany(_company);
        return author;
    }
}
