package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.apis.RoleController;
import com.mitrais.khotim.rmsspring.server.domains.Role;
import com.mitrais.khotim.rmsspring.server.domains.RoleResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RoleResourceAssembler extends ResourceAssemblerSupport<Role, RoleResource> {
    public RoleResourceAssembler() {
        super(RoleController.class, RoleResource.class);
    }

    @Override
    public RoleResource toResource(Role role) {
        return createResourceWithId(role.getId(), role);
    }

    @Override
    protected RoleResource instantiateResource(Role role) {
        return new RoleResource(role);
    }
}
