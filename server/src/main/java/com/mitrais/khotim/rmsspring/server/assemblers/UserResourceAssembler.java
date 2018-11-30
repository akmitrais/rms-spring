package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.apis.UserController;
import com.mitrais.khotim.rmsspring.server.domains.User;
import com.mitrais.khotim.rmsspring.server.domains.UserResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {
    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User user) {
        return createResourceWithId(user.getId(), user);
    }

    @Override
    protected UserResource instantiateResource(User user) {
        return new UserResource(user);
    }
}
