package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.BookResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.BookResource;
import com.mitrais.khotim.rmsspring.server.repositories.BookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @Mock
    BookResourceAssembler assembler;

    private Book book;
    private Book book2;
    private BookResourceAssembler bookAssembler;

    @Before
    public void setUp() {
        book = new Book("1234", "Space 1", "Khotim");
        book.setId(1L);
        book2 = new Book("9876", "Space 2", "Khotim");
        book2.setId(2L);

        bookAssembler = new BookResourceAssembler();
    }

    @InjectMocks
    BookService bookService;

    @Test
    public void findAll() {
        List<Book> books = Arrays.asList(book, book2);

        when(bookRepository.findAll()).thenReturn(books);
        when(assembler.toResources(Mockito.anyCollection())).thenReturn(bookAssembler.toResources(books));

        List<BookResource> resources = bookService.findByTitleAndStatus("", "");

        assertTrue(resources.stream().allMatch(r -> r.hasLink("self")));
        assertFalse(resources.isEmpty());
    }

    @Test
    public void findByTitle() {
        List<Book> books = Collections.singletonList(book);

        when(bookRepository.findByTitleContainingIgnoreCase("space")).thenReturn(books);
        when(assembler.toResources(Mockito.anyCollection())).thenReturn(bookAssembler.toResources(books));

        List<BookResource> resources = bookService.findByTitleAndStatus("space", "");

        assertTrue(resources.stream().allMatch(r -> r.hasLink("self")));
        assertFalse(resources.isEmpty());
    }

    @Test
    public void findByStatus() {
        List<Book> books = Arrays.asList(book, book2);

        when(bookRepository.findByStatusIgnoreCase(Book.SHELVED)).thenReturn(books);
        when(assembler.toResources(Mockito.anyCollection())).thenReturn(bookAssembler.toResources(books));

        List<BookResource> resources = bookService.findByTitleAndStatus("", Book.SHELVED);

        assertTrue(resources.stream().allMatch(r -> r.hasLink("self")));
        assertFalse(resources.isEmpty());
    }

    @Test
    public void findByTitleAndStatus() {
        List<Book> books = Arrays.asList(book, book2);

        when(assembler.toResources(Mockito.anyCollection())).thenReturn(bookAssembler.toResources(books));

        List<BookResource> resources = bookService.findByTitleAndStatus("space", Book.SHELVED);

        assertTrue(resources.stream().allMatch(r -> r.hasLink("self")));
        assertFalse(resources.isEmpty());
    }

    @Test
    public void findById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book testBook = bookService.findById(1L).orElse(null);

        assertNotNull(testBook);
        assertEquals(book.getId(), testBook.getId());
    }

    @Test
    public void save() {
        Book newBook = new Book("9876", "Space Adventure 1", "Khotim");
        newBook.setId(1L);

        BookResourceAssembler bookAssembler = new BookResourceAssembler();

        when(bookRepository.save(newBook)).thenReturn(newBook);
        when(assembler.toResource(newBook)).thenReturn(bookAssembler.toResource(newBook));

        BookResource testBook = bookService.save(newBook);

        assertTrue(testBook.hasLink("self"));
        assertNotNull(testBook.getContent());
    }

    @Test
    public void delete() {
        assertTrue(bookService.deleteById(Mockito.anyLong()));
    }
}