package sk.upjs.ics.users;

public interface UserDao {

    void update(User user);

    void updatePassword(User user, String password_hash);

    void delete(User user);

    User findById(Long id);

    Iterable<User> findAll();
}
