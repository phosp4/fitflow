package sk.upjs.ics.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.exceptions.AuthenticationException;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLAuthDao implements AuthDao {

    Connection connection;

    public SQLAuthDao(Connection connection) {
        this.connection = connection;
    }

    // todo
    private final String selectUserQuery = "SELECT * " +
            "FROM users WHERE (email = ?)";

    @Override
    public Principal authenticate(String email, String password) throws AuthenticationException {
        try (PreparedStatement pstmt = connection.prepareStatement(selectUserQuery)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            ArrayList<PrincipalWithPassword> principals = new ArrayList<>();

            while (rs.next()) {
                System.out.println(rs.getString("email"));
                User user = User.fromResultSet(rs);

                PrincipalWithPassword principalWithPassword = new PrincipalWithPassword();
                Principal principal = new Principal();

                principal.setId(user.getId());
                principal.setEmail(user.getEmail());
                //principal.setRole(user.getRole());
                principalWithPassword.setPrincipal(principal);
                principalWithPassword.setPassword(rs.getString("password_hash"));

                principals.add(principalWithPassword);
            }

//            if (!rs.next()) {
//                throw new NotFoundException("User with email " + email + " not found");
//            }

            if (principals.size() > 1) {
                System.out.println("Multiple users with the same email found.");
                throw new IllegalStateException("Multiple users with the same username or email found.");
            }

            if (principals.isEmpty()) {
                System.out.println("Invalid credentials1.");
                throw new AuthenticationException("Invalid credentials.");
            }

            var principal = principals.get(0);

            boolean ok = BCrypt.checkpw(password, principal.getPassword());
            if (!ok) {
                System.out.println("Invalid credentials2.");
                throw new AuthenticationException("Invalid credentials.");
            }

            return principal.getPrincipal();

        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
