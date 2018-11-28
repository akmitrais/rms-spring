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
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.BookResource;
import com.mitrais.khotim.rmsspring.server.services.BookService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BookController.class, secure = false)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private Book book;
    private BookResource bookR;
    private BookResource bookR2;
    private ObjectMapper mapper;

    private static final String BASE_PATH = "http://localhost";

    private String linkToAll;
    private String linkToOne;

    @Before
    public void setUp() {
        book = new Book("1234", "Space 2", "Khotim");
        book.setId(2L);
        
        bookR2 = new BookResource(book);
        
        book.setId(1L);
        book.setIsbn("9876");
        book.setTitle("Space 1");
        book.setAuthor("Khotim");
        
        bookR = new BookResource(book);
        
        mapper = new ObjectMapper();

        linkToAll = BASE_PATH + linkTo(methodOn(BookController.class).getAll("", ""));
        linkToOne = BASE_PATH + linkTo(methodOn(BookController.class).getOne(book.getId()));
    }

    private void verifyJson(final ResultActions action) throws Exception {
        action
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("bookId", is(book.getId().intValue())))
                .andExpect(jsonPath("isbn", is(book.getIsbn())))
                .andExpect(jsonPath("title", is(book.getTitle())))
                .andExpect(jsonPath("author", is(book.getAuthor())))
                .andExpect(jsonPath("_links.self.href", is(linkToOne)))
                .andExpect(jsonPath("_links.books.href", is(linkToAll)));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {
        List<BookResource> books = Arrays.asList(bookR, bookR2);

        Mockito.when(bookService.findByTitleAndStatus(Mockito.anyString(), Mockito.anyString())).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books"))
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is(linkToAll)))
                .andExpect(jsonPath("_embedded.bookList[*].size()", hasSize(2)));
    }

    @Test
    public void getOneBookWhenExists() throws Exception {
    	Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.when(bookService.toResource(Mockito.any(Book.class))).thenReturn(bookR);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/books/{id}", book.getId());
        ResultActions result = mockMvc.perform(request).andDo(print()).andExpect(status().isOk());

        verifyJson(result);
    }

    @Test
    public void getOneBookWhenNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", Mockito.anyLong()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void createNewBookReturnsCorrectResponse() throws Exception {
    	Mockito.when(bookService.save(Mockito.any(Book.class))).thenReturn(bookR);
    	
    	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/books")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.content(mapper.writeValueAsString(book));
    	
    	final ResultActions result = mockMvc.perform(request)
    			.andDo(print())
    			.andExpect(status().isCreated());
    	
    	verifyJson(result);
    }
    
    @Test
    public void createNewBookReturnsValidationError() throws Exception {
    	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/books")
    		  .contentType(MediaType.APPLICATION_JSON_VALUE)
    		  .content(mapper.writeValueAsString(new Book()));
      
      mockMvc.perform(request)
    		  .andDo(print())
    		  .andExpect(status().isBadRequest());
    }

    @Test
    public void updateExistingBookReturnsCorrectResponse() throws Exception {
        Mockito.when(bookService.save(Mockito.any(Book.class))).thenReturn(bookR);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/api/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(book));

        final ResultActions result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        verifyJson(result);
    }

    @Test
    public void updateExistingBookReturnsValidationError() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/api/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new Book()));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteExistingBookReturnsNothing() throws Exception {
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.when(bookService.deleteById(Mockito.anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonExistenceBookReturnsError() throws Exception {
        Mockito.when(bookService.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}