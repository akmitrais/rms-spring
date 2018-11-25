package com.mitrais.khotim.rmsspring.server.assemblers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.mitrais.khotim.rmsspring.server.apis.BookController;
import com.mitrais.khotim.rmsspring.server.domains.Book;

@Component
public class BookResourceAssembler implements ResourceAssembler<Book, Resource<Book>> {
    @Override
    public Resource<Book> toResource(Book book) {
        return new Resource<>(book,
                linkTo(methodOn(BookController.class).getOne(book.getId())).withSelfRel(),
                linkTo(methodOn(BookController.class).getAll("", "")).withRel("books"));
    }
}
