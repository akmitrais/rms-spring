package com.mitrais.khotim.rmsspring.server.domains;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignIn {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
