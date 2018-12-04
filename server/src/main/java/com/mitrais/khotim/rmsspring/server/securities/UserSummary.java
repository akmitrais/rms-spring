package com.mitrais.khotim.rmsspring.server.securities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String name;
    private String email;
}
