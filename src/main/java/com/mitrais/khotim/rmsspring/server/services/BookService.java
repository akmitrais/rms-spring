package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.BookResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.BookResource;
import com.mitrais.khotim.rmsspring.server.repositories.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
	private final BookRepository repository;
    private final BookResourceAssembler assembler;

    @Autowired
    public BookService(BookRepository repository, BookResourceAssembler assembler) {
    	this.repository = repository;
        this.assembler = assembler;
    }

    public List<BookResource> findByTitleAndStatus(String title, String status) {
    	Iterable<Book> books;
        
    	if (title.isEmpty() && status.isEmpty()) {
            books = repository.findAll();
        } else if (status.isEmpty()) {
        	books = repository.findByTitleContainingIgnoreCase(title);
        } else if (title.isEmpty()) {
        	books = repository.findByStatusIgnoreCase(status);
        } else {
        	books = repository.findByTitleContainingIgnoreCaseAndStatusIgnoreCase(title, status);
        }
        
		return assembler.toResources(books);
    }

    public BookResource toResource(Book book) {
        return assembler.toResource(book);
    }
    
    public Optional<Book> findById(Long id) {
    	return repository.findById(id);
    }

    public BookResource save(Book newBook) {
        return assembler.toResource(repository.save(newBook));
    }

    public boolean deleteById(Long id) {
        repository.deleteById(id);

        return true;
    }
}
