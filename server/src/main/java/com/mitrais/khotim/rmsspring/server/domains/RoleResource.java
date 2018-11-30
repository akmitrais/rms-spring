package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

@JsonPropertyOrder({"roleId", "name"})
public class RoleResource extends Resource<Role> {
    @JsonIgnore
    private final Role role;

    public RoleResource(Role role, Link... links) {
        super(role, links);
        this.role = role;
    }

    /**
     * Gets role id.
     *
     * @return The value of role id or throw exception if not role found.
     */
    public Long getRoleId() {
        if (this.role.getId() == null) {
            throw new RuntimeException("Couldn't find any role.");
        }

        return this.role.getId();
    }

    /**
     * Gets role name.
     *
     * @return The value of role name.
     */
    public RoleName getName() {
        return this.role.getName();
    }
}
