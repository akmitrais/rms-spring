package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.apis.LibraryController;
import com.mitrais.khotim.rmsspring.server.domains.ShelfJSON;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ShelfResourceAssembler extends ResourceAssemblerSupport<ShelfJSON, Resource<ShelfJSON>> {
	public ShelfResourceAssembler(Class<LibraryController> controllerClass, Class<Resource<ShelfJSON>> resourceType) {
		super(controllerClass, resourceType);
	}

	@Override
    public Resource<ShelfJSON> toResource(ShelfJSON shelf) {
        return new Resource<>(shelf,
        		linkTo(methodOn(LibraryController.class).getOne(shelf.getId())).withSelfRel(),
        		linkTo(methodOn(LibraryController.class).addBook(shelf.getId(), null, null))
        			.withRel("addBook"),
                linkTo(methodOn(LibraryController.class).removeBook(shelf.getId(), null, null))
                	.withRel("removeBook"),
                linkTo(methodOn(LibraryController.class).getAll()).withRel("libraries"));
    }
}
