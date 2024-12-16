package sk.upjs.ics.security;

import sk.upjs.ics.exceptions.AuthenticationException;

public interface AuthDao {
    Principal authenticate(String email, String password) throws AuthenticationException;
}
