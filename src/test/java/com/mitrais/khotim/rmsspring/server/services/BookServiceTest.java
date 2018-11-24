package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.repositories.BookRepository;
import com.mitrais.khotim.rmsspring.server.services.BookService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookService bookService;

    @Mock
    Book book;

    @Mock
    Book book2;

    @Test
    public void findAll() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book, book2));
        assertEquals(2, bookService.findByTitleAndStatus("", "").size());
    }

    @Test
    public void findByTitle() {
        when(bookRepository.findByTitleContainingIgnoreCase("space")).thenReturn(Collections.singletonList(book));
        assertEquals(1, bookService.findByTitleAndStatus("space", "").size());

        when(bookRepository.findByStatusIgnoreCase(Book.SHELVED)).thenReturn(Arrays.asList(book, book2));
        assertEquals(2, bookService.findByTitleAndStatus("", Book.SHELVED).size());

        when(bookRepository.findByTitleContainingIgnoreCaseAndStatusIgnoreCase("space", Book.NOT_SHELVED)).thenReturn(Arrays.asList(book, book2));
        assertEquals(2, bookService.findByTitleAndStatus("space", Book.NOT_SHELVED).size());
    }

    @Test
    public void findByStatus() {
        when(bookRepository.findByStatusIgnoreCase(Book.SHELVED)).thenReturn(Arrays.asList(book, book2));
        assertEquals(2, bookService.findByTitleAndStatus("", Book.SHELVED).size());
    }

    @Test
    public void findByTitleAndStatus() {
        when(bookRepository.findByTitleContainingIgnoreCaseAndStatusIgnoreCase("space", Book.NOT_SHELVED)).thenReturn(Arrays.asList(book, book2));
        assertEquals(2, bookService.findByTitleAndStatus("space", Book.NOT_SHELVED).size());
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

        when(bookRepository.save(newBook)).thenReturn(newBook);

        Book testBook = bookService.save(newBook);

        assertNotNull(testBook.getIsbn());
        assertNotNull(testBook.getTitle());
        assertNotNull(testBook.getAuthor());
        assertEquals("Space Adventure 1", testBook.getTitle());
        assertEquals(newBook.getShelf(), testBook.getShelf());
    }
}