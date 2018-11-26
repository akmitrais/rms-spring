package com.mitrais.khotim.rmsspring.server.apis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.services.BookService;
import com.mitrais.khotim.rmsspring.server.services.ShelfService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LibraryController.class, secure = false)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelfService shelfService;

    @MockBean
    private ShelfResourceAssembler assembler;

    @MockBean
    private BookService bookService;
    private Shelf shelf;
    private Shelf shelf2;
    private Book book;
    private ObjectMapper mapper;

    private static final String BASE_PATH = "http://localhost";

    private String linkToAll;
    private String linkToOne;

    @Before
    public void setUp() {
        shelf = new Shelf("Shelf A", 20);
        shelf.setId(1L);

        shelf2 = new Shelf("Shelf B", 35);
        shelf2.setId(2L);

        book = new Book();
        book.setId(1L);
        book.setShelf(shelf);

        linkToAll = BASE_PATH + linkTo(methodOn(LibraryController.class).getAll());
        linkToOne = BASE_PATH + linkTo(methodOn(LibraryController.class).getOne(shelf.getId()));

        mapper = new ObjectMapper();
    }

    private void verifyJson(final ResultActions action) throws Exception {
        action
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("id", is(shelf.getId().intValue())))
                .andExpect(jsonPath("name", is(shelf.getName())))
                .andExpect(jsonPath("maxCapacity", is(shelf.getMaxCapacity())))
                .andExpect(jsonPath("currentCapacity", is(shelf.getCurrentCapacity())))
                .andExpect(jsonPath("_links.self.href", is(linkToOne)))
                .andExpect(jsonPath("_links.libraries.href", is(linkToAll)));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {
        List<Shelf> shelves = Arrays.asList(shelf, shelf2);

        Mockito.when(shelfService.findAll()).thenReturn(shelves);
        Mockito.when(assembler.toResource(Mockito.any(Shelf.class))).thenCallRealMethod();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/libraries"))
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is(linkToAll)))
                .andExpect(jsonPath("_embedded.shelfList[*].size()", hasSize(2)));
    }

    @Test
    public void getOneShelfWhenExists() throws Exception {
        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(assembler.toResource(shelf)).thenCallRealMethod();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.get("/api/libraries/{id}", shelf.getId());
        ResultActions result = mockMvc.perform(request).andDo(print()).andExpect(status().isOk());

        verifyJson(result);
    }

    @Test
    public void getOneShelfWhenNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/libraries/{id}", Mockito.anyLong()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void createNewShelfReturnsCorrectResponse() throws Exception {
    	Mockito.when(shelfService.save(Mockito.any(Shelf.class))).thenReturn(shelf);
    	Mockito.when(assembler.toResource(Mockito.any(Shelf.class))).thenCallRealMethod();
    	
    	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/libraries")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.content(mapper.writeValueAsString(shelf));
    	
    	final ResultActions result = mockMvc.perform(request)
    			.andDo(print())
    			.andExpect(status().isCreated());
    	
    	verifyJson(result);
    }
    
    @Test
    public void createNewShelfReturnsValidationError() throws Exception {
    	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/libraries")
    		  .contentType(MediaType.APPLICATION_JSON_VALUE)
    		  .content(mapper.writeValueAsString(new Shelf()));
      
      mockMvc.perform(request)
    		  .andDo(print())
    		  .andExpect(status().isBadRequest());
    }

    @Test
    public void updateExistingShelfReturnsCorrectResponse() throws Exception {
        Mockito.when(shelfService.save(Mockito.any(Shelf.class))).thenReturn(shelf);
        Mockito.when(assembler.toResource(Mockito.any(Shelf.class))).thenCallRealMethod();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/api/libraries/{id}", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(shelf));

        final ResultActions result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        verifyJson(result);
    }

    @Test
    public void updateExistingShelfReturnsValidationError() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/api/libraries/{id}", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new Shelf()));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteExistingShelfReturnsNothing() throws Exception {
        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(shelfService.deleteById(Mockito.anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/libraries/{id}", shelf.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonExistenceShelfReturnsError() throws Exception {
        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/libraries/{id}", shelf.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void addBookWhenShelfNotExists() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messages.shelf", is("There's no shelf found with id " + shelf.getId())));
    }

    @Test
    public void addBookWhenShelfReachedMaxCapacity() throws Exception {
        shelf.setCurrentCapacity(shelf.getMaxCapacity());

        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("messages.shelf", is("Shelf " + shelf.getName() + " already reached maximum capacity")));
    }

    @Test
    public void addBookWhenBookNotExists() throws Exception {
        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messages.book", is("There's no book found with id " + book.getId())));
    }

    @Test
    public void addBookWhenBookExistsInShelf() throws Exception {
        shelf.setBooks(Collections.singletonList(book));

        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("messages.shelf", is("Book " + book.getTitle() + " already exists in shelf " + shelf.getName())));
    }

    @Test
    public void addBookWhenBookIsShelved() throws Exception {
        book.setStatus(Book.SHELVED);

        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
        		.andExpect(jsonPath("messages.book", is("Book " + book.getTitle() + " is already shelved in shelf " + book.getShelf().getName())));
    }

    @Test
    public void addBookSuccess() throws Exception {
    	shelf.setBooks(Collections.singletonList(book));
    	
        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf2));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.when(shelfService.addBook(Mockito.any(Shelf.class), Mockito.any(Book.class)))
        	.thenReturn(shelf);
        Mockito.when(assembler.toResource(Mockito.any(Shelf.class))).thenCallRealMethod();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        ResultActions result = mockMvc.perform(request).andDo(print()).andExpect(status().isOk());
        
        verifyJson(result);
    }

    @Test
    public void removeBookWhenShelfNotExists() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/libraries/{id}/removeBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
        		.andExpect(jsonPath("messages.shelf", is("There's no shelf found with id " + shelf.getId())));
    }

    @Test
    public void removeBookWhenBookNotExists() throws Exception {
        Mockito.when(shelfService.findById(shelf.getId())).thenReturn(Optional.of(shelf));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/libraries/{id}/removeBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
        		.andExpect(jsonPath("messages.book", is("There's no book found with id " + book.getId())));
    }

    @Test
    public void removeBookWhenBookNotInShelf() throws Exception {
        Mockito.when(shelfService.findById(shelf.getId())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(book.getId())).thenReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/libraries/{id}/removeBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("messages.shelf", is("There's no book " + book.getTitle() + " in shelf " + shelf.getName())));
    }

    @Test
    public void removeBookSuccess() throws Exception {
        shelf2.setBooks(Collections.singletonList(book));

        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf2));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.when(shelfService.removeBook(Mockito.any(Shelf.class), Mockito.any(Book.class)))
        	.thenReturn(shelf);
        Mockito.when(assembler.toResource(Mockito.any(Shelf.class))).thenCallRealMethod();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/removeBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        ResultActions result = mockMvc.perform(request).andDo(print()).andExpect(status().isOk());
        
        verifyJson(result);
    }
}