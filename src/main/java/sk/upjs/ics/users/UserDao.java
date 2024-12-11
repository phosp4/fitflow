package sk.upjs.ics.users;

import java.io.File;

public interface UserDao {

    void loadFromCsv(File file);

    void update(User user);

    void updatePassword(User user, String password_hash);

    void delete(User user);

    User findById(Long id);

    Iterable<User> findAll();
}
