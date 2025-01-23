package sk.upjs.ics.models;

import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.daos.interfaces.RoleDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserModel {
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final CreditTransactionDao transactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final RoleDao roleDao = Factory.INSTANCE.getRoleDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();

    public User getCurrentUser() {
        Principal principal = Auth.INSTANCE.getPrincipal();
        return userDao.findById(principal.getId());
    }

    public void updateCurrentUserCreditBalance(long addBalance) {
        // create a transaction first
        CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setUser(getCurrentUser());
        creditTransaction.setAmount(addBalance);
        creditTransaction.setCreditTransactionType(Factory.INSTANCE.getTransactionTypeDao().findById(2L));
        creditTransaction.setCreatedAt(new Timestamp(System.currentTimeMillis()).toInstant());
        transactionDao.create(creditTransaction);

        // now update the balance
        User user = getCurrentUser();
        user.setCreditBalance(getCurrentUser().getCreditBalance() + addBalance);
        userDao.updateBalance(user);
    }

    public ArrayList<String> getTrainers() {
        ArrayList<String> trainers = new ArrayList<>();

        userDao.findAll().forEach(user -> {
            if (user.getRole().getId() == 3) {
                trainers.add(user.getFirstName() + " " + user.getLastName());
            }
        });

        return trainers;
    }


    public User getUserByName(String name) {
        String[] parts = name.split(" ");
        for (User user : userDao.findAll()) {
            if (user.getFirstName().equals(parts[0]) && user.getLastName().equals(parts[1])) {
                return user;
            }
        }
        return null;
    }

    public User getUserById(long id) {
        return userDao.findById(id);
    }

    public User findByEmail(String email) {
        ArrayList<User> users = userDao.findAll();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public void registerUser(String email, String password1, String password2, String firstName, String lastName, String phone, LocalDate birthDate) {
        if (findByEmail(email) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (!password1.equals(password2)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        var salt = BCrypt.gensalt();
        var passwordHash = BCrypt.hashpw(password1, salt); // both password1 and password2 are the same

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setRole(roleDao.findById(2L));
        user.setLastName(lastName);
        user.setCreditBalance(0L);
        user.setPhone(phone);
        user.setBirthDate(birthDate);
        user.setRole(Factory.INSTANCE.getRoleDao().findById(1L));
        userDao.create(user, salt, passwordHash);
    }
}