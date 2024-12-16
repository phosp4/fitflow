package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Role;

import java.util.ArrayList;

public interface RoleDao {

    void create(String role);

    void delete(Role role);

    void update(Role role);

    Role findById(Long id);

    ArrayList<Role> findAll();
}
