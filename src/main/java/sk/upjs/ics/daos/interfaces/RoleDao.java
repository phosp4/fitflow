package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Role;

public interface RoleDao {
    void create(String role);

    void update(Role role);

    void delete(Role role);

    Role findById(Long id);

    Iterable<Role> findAll();
}
