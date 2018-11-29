package com.mitrais.khotim.rmsspring.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    @JsonProperty("userId")
    private long id;
    private String email;
    private String password;
    private String name;
}
