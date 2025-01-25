package sk.upjs.ics.security;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.exceptions.AuthenticationException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.util.ArrayList;

/**
 * SQLAuthDao is an implementation of the AuthDao interface
 * that provides methods to authenticate users against the database.
 */
public class SQLAuthDao implements AuthDao {

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLAuthDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLAuthDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * Extracts a list of PrincipalWithPassword objects from the given ResultSet.
     */
    private final ResultSetExtractor<ArrayList<PrincipalWithPassword>> resultSetExtractor = rs -> {
        ArrayList<PrincipalWithPassword> principals = new ArrayList<>();

        while (rs.next()) {
            User user = User.fromResultSet(rs);

            PrincipalWithPassword principalWithPassword = new PrincipalWithPassword();
            Principal principal = new Principal();

            principal.setId(user.getId());
            principal.setEmail(user.getEmail());
            //principal.setRole(user.getRole()); TODO
            principalWithPassword.setPrincipal(principal);
            principalWithPassword.setPassword(rs.getString("password_hash"));

            principals.add(principalWithPassword);
        }

        return principals;
    };

    /**
     * Authenticates a user by their email and password.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return the authenticated Principal
     * @throws AuthenticationException if the authentication fails
     */
    @Override
    public Principal authenticate(String email, String password) throws AuthenticationException {
        ArrayList<PrincipalWithPassword> principals = jdbcOperations.query("SELECT * FROM users WHERE email = ?", resultSetExtractor, email);

        if (principals == null || principals.isEmpty()) {
            throw new NotFoundException("User with email " + email + " not found");
        }

        if (principals.size() > 1) {
            System.out.println("Multiple users with the same email found.");
            throw new IllegalStateException("Multiple users with the same username or email found.");
        }

        var principal = principals.getFirst();

        boolean ok = BCrypt.checkpw(password, principal.getPassword());
        if (!ok) {
            throw new AuthenticationException("Invalid credentials.");
        }

        return principal.getPrincipal();
    }
}