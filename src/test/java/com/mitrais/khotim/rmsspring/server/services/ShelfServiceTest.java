package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.domains.ShelfResource;
import com.mitrais.khotim.rmsspring.server.repositories.ShelfRepository;
import com.mitrais.khotim.rmsspring.server.services.ShelfService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShelfServiceTest {
    @Mock
    ShelfRepository shelfRepository;

    @Mock
    ShelfResourceAssembler assembler;

    @InjectMocks
    ShelfService shelfService;

    @Mock
    Shelf shelf;

    @Mock
    Shelf shelf2;

    private final ShelfResourceAssembler resourceAssembler = new ShelfResourceAssembler();

    @Test
    public void addBook() {
        Shelf newShelf = new Shelf();
        newShelf.setId(1L);

        Book newBook = new Book();

        when(shelfRepository.save(Mockito.any(Shelf.class))).thenReturn(newShelf);
        when(assembler.toResource(Mockito.any(Shelf.class))).thenReturn(resourceAssembler.toResource(newShelf));

        ShelfResource testShelf = shelfService.addBook(newShelf, newBook);

        assertEquals(1, testShelf.getCurrentCapacity());
        assertNotNull(testShelf.getBooks());
        assertEquals(Book.SHELVED, newBook.getStatus());
        assertNotNull(newBook.getShelf());
    }

    @Test
    public void removeBook() {
        Shelf newShelf = new Shelf();
        newShelf.setId(1L);
        newShelf.setCurrentCapacity(1);

        Book newBook = new Book();

        when(shelfRepository.save(Mockito.any(Shelf.class))).thenReturn(newShelf);
        when(assembler.toResource(Mockito.any(Shelf.class))).thenReturn(resourceAssembler.toResource(newShelf));

        ShelfResource testShelf = shelfService.removeBook(newShelf, newBook);

        assertEquals(0, testShelf.getCurrentCapacity());
        assertTrue(testShelf.getBooks().isEmpty());
        assertEquals(Book.NOT_SHELVED, newBook.getStatus());
        assertNull(newBook.getShelf());
    }

    @Test
    public void findById() {
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));

        Shelf testShelf = shelfService.findById(1L).orElse(null);

        assertNotNull(testShelf);
        assertEquals(shelf.getId(), testShelf.getId());
    }

    @Test
    public void findAll() {
        List<Shelf> shelves = Arrays.asList(shelf, shelf2);

        when(shelfRepository.findAll()).thenReturn(shelves);
        when(assembler.toResources(Mockito.anyCollection())).thenCallRealMethod();

        assertEquals(2, shelfService.findAll().size());
    }

    @Test
    public void save() {
        Shelf newShelf= new Shelf("Shelf 1", 20);
        newShelf.setId(1L);

        when(shelfRepository.save(newShelf)).thenReturn(newShelf);
        when(assembler.toResource(Mockito.any(Shelf.class))).thenReturn(resourceAssembler.toResource(newShelf));

        ShelfResource testShelf = shelfService.save(newShelf);

        assertNotNull(testShelf.getName());
        assertEquals("Shelf 1", testShelf.getName());
        assertEquals(newShelf.getMaxCapacity(), testShelf.getMaxCapacity());
    }

    @Test
    public void delete() {
        assertTrue(shelfService.deleteById(Mockito.anyLong()));
    }
}