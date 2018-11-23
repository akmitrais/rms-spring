package com.mitrais.khotim.rmsspring.services;

import com.mitrais.khotim.rmsspring.domains.Book;
import com.mitrais.khotim.rmsspring.domains.Shelf;
import com.mitrais.khotim.rmsspring.repositories.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepository;

    @Autowired
    public ShelfService(ShelfRepository shelfRepository) {
        this.shelfRepository = shelfRepository;
    }

    /**
     * Adds book into shelf.
     *
     * @param shelf Existing shelf.
     * @param book Existing book.
     * @return Shelf instance.
     */
    public Shelf addBook(Shelf shelf, Book book) {
        book.setStatus(Book.SHELVED);
        shelf.setCurrentCapacity(shelf.getCurrentCapacity() + 1);
        shelf.addBook(book);

        return shelfRepository.save(shelf);
    }

    /**
     * Removes book from shelf.
     *
     * @param shelf Existing shelf.
     * @param book Existing book.
     * @return Shelf instance.
     */
    public Shelf removeBook(Shelf shelf, Book book) {
        book.setStatus(Book.NOT_SHELVED);
        shelf.setCurrentCapacity(shelf.getCurrentCapacity() - 1);
        shelf.removeBook(book);

        return shelfRepository.save(shelf);
    }

    public Optional<Shelf> findById(Long id) {
        return shelfRepository.findById(id);
    }

    public List<Shelf> findAll() {
        return shelfRepository.findAll();
    }

    Shelf save(Shelf shelf) {
        return shelfRepository.save(shelf);
    }
}
