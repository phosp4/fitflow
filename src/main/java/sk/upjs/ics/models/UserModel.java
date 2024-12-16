package sk.upjs.ics.models;

import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

public class UserModel {
    private UserDao userDao = Factory.INSTANCE.getUserDao();

    public User getCurrentUser() {
        Principal principal = Auth.INSTANCE.getPrincipal();
        return userDao.findById(principal.getId());
    }

    public void updateCurrentUserCreditBalance(long newBalance) {
        User user = getCurrentUser();
        user.setCreditBalance(newBalance);
        userDao.updateBalance(user);
    }
}