package com.mitrais.khotim.rmsspring.server.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitrais.khotim.rmsspring.server.assemblers.ShelfResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.domains.ShelfResource;
import com.mitrais.khotim.rmsspring.server.services.BookService;
import com.mitrais.khotim.rmsspring.server.services.ShelfService;
import com.mitrais.khotim.rmsspring.server.validations.ShelfValidation;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LibraryController.class, secure = false)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelfService shelfService;

    @MockBean
    private BookService bookService;
    private Shelf shelf;
    private ShelfResource shelfR;
    private ShelfResource shelfR2;
    private Book book;
    private ObjectMapper mapper;

    private static final String BASE_PATH = "http://localhost";

    private String linkToAll;
    private String linkToOne;

    @Before
    public void setUp() {
        ShelfResourceAssembler assembler = new ShelfResourceAssembler();

        shelf = new Shelf("Shelf B", 35);
        shelf.setId(2L);

        shelfR2 = assembler.toResource(shelf);

        shelf = new Shelf("Shelf A", 20);
        shelf.setId(1L);

        shelfR = assembler.toResource(shelf);

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
                .andExpect(jsonPath("shelfId", is(shelf.getId().intValue())))
                .andExpect(jsonPath("name", is(shelf.getName())))
                .andExpect(jsonPath("maxCapacity", is(shelf.getMaxCapacity())))
                .andExpect(jsonPath("currentCapacity", is(shelf.getCurrentCapacity())))
                .andExpect(jsonPath("_links.self.href", is(not(empty()))));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {
        List<ShelfResource> shelves = Arrays.asList(shelfR, shelfR2);

        Mockito.when(shelfService.findAll()).thenReturn(shelves);

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
        Mockito.when(shelfService.toResource(Mockito.any(Shelf.class))).thenReturn(shelfR);

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
    	Mockito.when(shelfService.save(Mockito.any(Shelf.class))).thenReturn(shelfR);
    	
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
        Mockito.when(shelfService.save(Mockito.any(Shelf.class))).thenReturn(shelfR);

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
    public void addBookReturnsCorrectResponse() throws Exception {
        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        
        ShelfValidation validation = new ShelfValidation();
        validation.setValid(true);
        
        Mockito.when(shelfService.validateAddBook(Mockito.any(Shelf.class), Mockito.any(Book.class)))
        	.thenReturn(validation);
        Mockito.when(shelfService.toResource(Mockito.any(Shelf.class))).thenReturn(shelfR);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        ResultActions result = mockMvc.perform(request).andExpect(status().isOk());
        
        verifyJson(result);
    }
    
    @Test
    public void addBookValidationError() throws Exception {
    	Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.when(shelfService.validateAddBook(Mockito.any(Shelf.class), Mockito.any(Book.class)))
        	.thenReturn(new ShelfValidation());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/addBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void removeBookReturnsCorrectResponse() throws Exception {
        shelf.setBooks(Collections.singletonList(book));

        Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        
        ShelfValidation validation = new ShelfValidation();
        validation.setValid(true);
        
        Mockito.when(shelfService.validateRemoveBook(Mockito.any(Shelf.class), Mockito.any(Book.class)))
        	.thenReturn(validation);
        Mockito.when(shelfService.toResource(Mockito.any(Shelf.class))).thenReturn(shelfR);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/removeBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        ResultActions result = mockMvc.perform(request).andDo(print()).andExpect(status().isOk());
        
        verifyJson(result);
    }
    
    @Test
    public void removeBookValidationError() throws Exception {
    	Mockito.when(shelfService.findById(Mockito.anyLong())).thenReturn(Optional.of(shelf));
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.when(shelfService.validateRemoveBook(Mockito.any(Shelf.class), Mockito.any(Book.class)))
        	.thenReturn(new ShelfValidation());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        		.patch("/api/libraries/{id}/removeBook", shelf.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }
}