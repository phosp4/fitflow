package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Role;

import java.io.File;
import java.util.ArrayList;

public interface RoleDao {

    void loadFromCsv(File file);

    void create(Role role);

    void delete(Role role);

    void update(Role role);

    Role findById(Long id);

    ArrayList<Role> findAll();
}
