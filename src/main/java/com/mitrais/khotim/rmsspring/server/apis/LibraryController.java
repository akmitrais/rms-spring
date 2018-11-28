package com.mitrais.khotim.rmsspring.server.apis;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.domains.ShelfJSON;
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
    public ResponseEntity<?> getAll() {
        List<ShelfJSON> shelves = StreamSupport.stream(shelfService.findAll().spliterator(), false)
			.map(ShelfJSON::new)
			.collect(Collectors.toList());
		
        return ResponseEntity.ok(assembler.toResources(shelves));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Shelf shelf = shelfService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find shelf with id " + id));

        return ResponseEntity.ok(assembler.toResource(new ShelfJSON(shelf)));
    }
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Shelf newShelf, BindingResult errors, WebRequest request) throws URISyntaxException {
        if (errors.hasErrors()) {
        	return ResponseEntity
        			.status(HttpStatus.BAD_REQUEST)
        			.body(new ErrorDetails(ValidationMessage.getMessages(errors), request.getDescription(false)));
        }

        Resource<ShelfJSON> resource = assembler.toResource(new ShelfJSON(shelfService.save(newShelf)));

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable final Long id, @RequestBody @Valid Shelf newShelf, BindingResult errors, WebRequest request) {
        if (errors.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDetails(ValidationMessage.getMessages(errors), request.getDescription(false)));
        }

        Shelf updatedShelf= shelfService.findById(id)
                .map(shelf -> {
                    shelf.setName(newShelf.getName());
                    shelf.setMaxCapacity(newShelf.getMaxCapacity());

                    return shelfService.save(shelf);
                })
                .orElseGet(() -> {
                    newShelf.setId(id);
                    return shelfService.save(newShelf);
                });

        Resource<ShelfJSON> resource = assembler.toResource(new ShelfJSON(updatedShelf));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
    	shelfService.findById(id)
    		.orElseThrow(() -> new ResourceNotFoundException("Cannot find shelf with id " + id));

        return shelfService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().body("Cannot proceed your request on this resource.");
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
        Book book = bookService.findById(pBook.getId()).orElseThrow(() -> new ResourceNotFoundException("Cannot find book with id " + id));

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

                responseBody = assembler.toResource(new ShelfJSON(shelfService.removeBook(shelf, book)));
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
            
            responseBody = assembler.toResource(new ShelfJSON(shelfService.addBook(shelf, book)));
            httpStatus = HttpStatus.OK;
        } while (false);
        
        if (responseBody.getClass() == ErrorDetails.class) {
        	((ErrorDetails) responseBody).setMessages(messages);
        }
        
        return ResponseEntity.status(httpStatus).body(responseBody);
    }
}
