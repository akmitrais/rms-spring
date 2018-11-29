package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.domains.ShelfResource;
import com.mitrais.khotim.rmsspring.server.repositories.ShelfRepository;
import com.mitrais.khotim.rmsspring.server.validations.ShelfValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepository;
    private final ShelfResourceAssembler assembler;

    @Autowired
    public ShelfService(ShelfRepository shelfRepository, ShelfResourceAssembler assembler) {
        this.shelfRepository = shelfRepository;
        this.assembler = assembler;
    }

    /**
     * Adds book into shelf.
     *
     * @param shelf Existing shelf.
     * @param book Existing book.
     * @return Shelf instance.
     */
    public ShelfResource addBook(Shelf shelf, Book book) {
        book.setStatus(Book.SHELVED);
        shelf.setCurrentCapacity(shelf.getCurrentCapacity() + 1);
        shelf.getBooks().add(book);
        book.setShelf(shelf);

        return assembler.toResource(shelfRepository.save(shelf));
    }

    /**
     * Removes book from shelf.
     *
     * @param shelf Existing shelf.
     * @param book Existing book.
     * @return Shelf instance.
     */
    public ShelfResource removeBook(Shelf shelf, Book book) {
        book.setStatus(Book.NOT_SHELVED);
        shelf.setCurrentCapacity(shelf.getCurrentCapacity() - 1);
        shelf.getBooks().remove(book);
        book.setShelf(null);

        return assembler.toResource(shelfRepository.save(shelf));
    }

    public Optional<Shelf> findById(Long id) {
        return shelfRepository.findById(id);
    }

    public List<ShelfResource> findAll() {
        return assembler.toResources(shelfRepository.findAll());
    }

    public ShelfResource save(Shelf shelf) {
        return assembler.toResource(shelfRepository.save(shelf));
    }

    public boolean deleteById(Long id) {
        shelfRepository.deleteById(id);

        return true;
    }

    public ShelfResource toResource(Shelf shelf) {
        return assembler.toResource(shelf);
    }

    public ShelfValidation validateAddBook(Shelf shelf, Book book) {
    	ShelfValidation validation = new ShelfValidation();
    	
    	if (shelf.getCurrentCapacity() == shelf.getMaxCapacity()) {
    		validation.addMessage("shelf", "Shelf " + shelf.getName() + " already reached maximum capacity");
    	} else if (shelf.getBooks().contains(book)) {
    		validation.addMessage("shelf", "Book " + book.getTitle() + " already exists in shelf " + shelf.getName());
    	} else if (book.getStatus().equals(Book.SHELVED)) {
    		validation.addMessage("book", "Book " + book.getTitle() + " is already shelved in shelf " + book.getShelf().getName());
    	} else {
    		validation.setValid(true);
    		addBook(shelf, book);
    	}
    	
    	return validation;
    }
    
	public ShelfValidation validateRemoveBook(Shelf shelf, Book book) {
		ShelfValidation validation = new ShelfValidation();
		
		if (!shelf.getBooks().contains(book)) {
            validation.addMessage("shelf", "There's no book " + book.getTitle() + " in shelf " + shelf.getName());
        } else {
			validation.setValid(true);
			removeBook(shelf, book);
		}
		
		return validation;
	}
}
