package sk.upjs.ics.security;

import lombok.Data;

@Data
public class Principal {
    private Long id;
    private String email;
    private String username;
    //private Role role; todo doplnit tu asi dalsie veci
}