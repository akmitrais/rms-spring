package com.mitrais.khotim.rmsspring.server.apis;

import com.mitrais.khotim.rmsspring.server.assemblers.BookResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.exceptions.ErrorDetails;
import com.mitrais.khotim.rmsspring.server.exceptions.ResourceNotFoundException;
import com.mitrais.khotim.rmsspring.server.services.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/books", produces = MediaTypes.HAL_JSON_VALUE)
public class BookController {
    private final BookService bookService;
    private final BookResourceAssembler assembler;

    @Autowired
    public BookController(BookService bookService, BookResourceAssembler assembler) {
        this.bookService = bookService;
        this.assembler = assembler;
    }

    @GetMapping
    public Resources<Resource<Book>> getAll(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String status
    ) {
        List<Resource<Book>> books = bookService.findByTitleAndStatus(title, status).stream().map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(books, linkTo(methodOn(BookController.class).getAll(title, status)).withSelfRel());
    }

    @GetMapping("/{id}")
    public Resource<Book> getOne(@PathVariable Long id) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find book with id " + id));

        return assembler.toResource(book);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> newBook(@RequestBody @Valid Book newBook, BindingResult errors) throws URISyntaxException {
        if (errors.hasErrors()) {
            return new ResponseEntity<>(ErrorDetails.getMessages(errors), HttpStatus.OK);
        }

        Resource<Book> resource = assembler.toResource(bookService.save(newBook));

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }
}