package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.domains.ShelfResource;
import com.mitrais.khotim.rmsspring.server.repositories.ShelfRepository;
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
        shelf.addBook(book);

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
        shelf.removeBook(book);

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
}
