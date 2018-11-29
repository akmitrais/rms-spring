package com.mitrais.khotim.rmsspring.server.apis;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class RootController {
    @GetMapping
    ResponseEntity<ResourceSupport> root() {
        ResourceSupport resourceSupport = new ResourceSupport();

        resourceSupport.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        resourceSupport.add(linkTo(methodOn(BookController.class).getAll("", "")).withRel("books"));
        resourceSupport.add(linkTo(methodOn(LibraryController.class).getAll()).withRel("libraries"));

        return ResponseEntity.ok(resourceSupport);
    }

}
