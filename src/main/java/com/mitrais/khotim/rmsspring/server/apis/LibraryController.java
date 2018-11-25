package com.mitrais.khotim.rmsspring.server.apis;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.exceptions.ErrorDetails;
import com.mitrais.khotim.rmsspring.server.exceptions.ResourceNotFoundException;
import com.mitrais.khotim.rmsspring.server.services.BookService;
import com.mitrais.khotim.rmsspring.server.services.ShelfService;
import com.mitrais.khotim.rmsspring.server.validations.ValidationMessage;

@RestController
@RequestMapping(value = "/api/libraries", produces = MediaTypes.HAL_JSON_VALUE)
public class LibraryController {
    private final ShelfService shelfService;
    private final BookService bookService;
    private final ShelfResourceAssembler assembler;

    @Autowired
    public LibraryController(ShelfService shelfService, BookService bookService, ShelfResourceAssembler assembler) {
        this.shelfService = shelfService;
        this.bookService = bookService;
        this.assembler = assembler;
    }

    @GetMapping
    public Resources<Resource<Shelf>> getAll() {
        List<Resource<Shelf>> shelves = shelfService.findAll().stream().map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(shelves,
        		linkTo(methodOn(LibraryController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public Resource<Shelf> getOne(@PathVariable Long id) {
        Shelf shelf = shelfService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find shelf with id " + id));

        return assembler.toResource(shelf);
    }
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Shelf newShelf, BindingResult errors, WebRequest request) throws URISyntaxException {
        if (errors.hasErrors()) {
        	return ResponseEntity
        			.status(HttpStatus.BAD_REQUEST)
        			.body(new ErrorDetails(ValidationMessage.getMessages(errors), request.getDescription(false)));
        }

        Resource<Shelf> resource = assembler.toResource(shelfService.save(newShelf));

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PatchMapping("/{id}/addBook")
    public ResponseEntity<?> addBook(@PathVariable final long id, @RequestBody Book pBook, WebRequest request) {
        return doOperation(id, pBook, "add", request);
    }

    @PatchMapping("/{id}/removeBook")
    public ResponseEntity<?> removeBook(@PathVariable final long id, @RequestBody Book pBook, WebRequest request) {
        return doOperation(id, pBook, "remove", request);
    }

    private ResponseEntity<?> doOperation(final long id, Book pBook, String operation, WebRequest request) {
        Map<String, String> messages = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Object responseBody = new ErrorDetails(messages, request.getDescription(false));

        Shelf shelf = shelfService.findById(id).orElse(null);
        Book book = bookService.findById(pBook.getId()).orElse(null);

        do {
        	if (shelf == null) {
        		messages.put("shelf", "There's no shelf found with id " + id);
        		httpStatus = HttpStatus.NOT_FOUND;
        		break;
        	}
        	
        	if (book == null) {
                messages.put("book", "There's no book found with id " + pBook.getId());
                httpStatus = HttpStatus.NOT_FOUND;
                break;
        	}
        	
        	if (operation.equals("remove")) {
                if (!shelf.getBooks().contains(book)) {
                    messages.put("shelf", "There's no book " + book.getTitle() + " in shelf " + shelf.getName());
                    break;
                }

                responseBody = assembler.toResource(shelfService.removeBook(shelf, book));
                httpStatus = HttpStatus.OK;
                break;
            }
        	
        	if (shelf.getCurrentCapacity() == shelf.getMaxCapacity()) {
                messages.put("shelf", "Shelf " + shelf.getName() + " already reached maximum capacity");
                break;
            }
        	
        	if (shelf.getBooks().contains(book)) {
                messages.put("shelf", "Book " + book.getTitle() + " already exists in shelf " + shelf.getName());
                break;
            }

            if (book.getStatus().equals(Book.SHELVED)) {
            	messages.put("book", "Book " + book.getTitle() + " is already shelved in shelf " + book.getShelf().getName());
                break;
            }
            
            responseBody = assembler.toResource(shelfService.addBook(shelf, book));
            httpStatus = HttpStatus.OK;
        } while (false);
        
        if (responseBody.getClass() == ErrorDetails.class) {
        	((ErrorDetails) responseBody).setMessages(messages);
        }
        
        return ResponseEntity.status(httpStatus).body(responseBody);
    }
}
