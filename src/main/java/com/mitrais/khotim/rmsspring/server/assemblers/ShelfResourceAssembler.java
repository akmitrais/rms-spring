package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.apis.LibraryController;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ShelfResourceAssembler implements ResourceAssembler<Shelf, Resource<Shelf>> {
    @Override
    public Resource<Shelf> toResource(Shelf shelf) {
        return new Resource<>(shelf,
        		linkTo(methodOn(LibraryController.class).getOne(shelf.getId())).withSelfRel(),
        		linkTo(methodOn(LibraryController.class).addBook(shelf.getId(), null, null))
        			.withRel("addBook"),
                linkTo(methodOn(LibraryController.class).removeBook(shelf.getId(), null, null))
                	.withRel("removeBook"),
                linkTo(methodOn(LibraryController.class).getAll()).withRel("libraries"));
    }
}
