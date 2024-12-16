package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.User;

import java.io.File;
import java.util.ArrayList;

public interface UserDao {

    void loadFromCsv(File file);

    void delete(User user);

    void update(User user);

    void updatePassword(User user, String password_hash);

    User findById(Long id);

    ArrayList<User> findAll();
}
