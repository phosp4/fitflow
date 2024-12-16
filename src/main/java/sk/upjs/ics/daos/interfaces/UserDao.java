package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.User;

import java.io.File;

public interface UserDao {

    void loadFromCsv(File file);

    void delete(User user);

    void update(User user);
    void updateBalance(User user);

    void updatePassword(User user, String password_hash);

    User findById(Long id);

    Iterable<User> findAll();
}
