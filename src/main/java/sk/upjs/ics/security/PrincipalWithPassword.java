package sk.upjs.ics.security;

import lombok.Data;

@Data
public class PrincipalWithPassword {
    private Principal principal;
    private String password;
}
