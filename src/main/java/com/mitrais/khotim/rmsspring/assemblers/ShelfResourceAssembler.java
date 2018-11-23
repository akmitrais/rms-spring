package com.mitrais.khotim.rmsspring.assemblers;

import com.mitrais.khotim.rmsspring.apis.LibraryController;
import com.mitrais.khotim.rmsspring.domains.Shelf;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ShelfResourceAssembler implements ResourceAssembler<Shelf, Resource<Shelf>> {
    @Override
    public Resource<Shelf> toResource(Shelf shelf) {
        return new Resource<>(shelf,
                linkTo(methodOn(LibraryController.class).getOne(shelf.getId())).withSelfRel(),
                linkTo(methodOn(LibraryController.class).getAll()).withRel("libraries"));
    }
}
