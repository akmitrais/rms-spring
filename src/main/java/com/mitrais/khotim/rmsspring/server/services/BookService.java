package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.repositories.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findByTitleAndStatus(String title, String status) {
        if (title.isEmpty() && status.isEmpty()) {
            return repository.findAll();
        }

        if (status.isEmpty()) {
            return repository.findByTitleContainingIgnoreCase(title);
        }

        if (title.isEmpty()) {
            return repository.findByStatusIgnoreCase(status);
        }

        return repository.findByTitleContainingIgnoreCaseAndStatusIgnoreCase(title, status);
    }

    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    public Book save(Book newBook) {
        return repository.save(newBook);
    }

    public boolean deleteById(Long id) {
        repository.deleteById(id);

        return true;
    }
}
