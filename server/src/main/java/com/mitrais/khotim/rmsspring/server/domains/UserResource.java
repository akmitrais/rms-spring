package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

@JsonPropertyOrder({"userId", "name", "email"})
public class UserResource extends Resource<User> {
    @JsonIgnore
    private final User user;

    public UserResource(User user, Link... links) {
        super(user, links);
        this.user = user;
    }

    /**
     * Gets user id.
     *
     * @return The value of user id.
     * @throws RuntimeException if user not found.
     */
    public Long getUserId() {
        if (this.user.getId() == null) {
            throw new RuntimeException("Couldn't find any user.");
        }

        return this.user.getId();
    }

    /**
     * Gets user name.
     *
     * @return The value of user name.
     */
    public String getName() {
        return this.user.getName();
    }

    /**
     * Gets user email.
     *
     * @return The value of user email.
     */
    public String getEmail() {
        return this.user.getEmail();
    }
}
