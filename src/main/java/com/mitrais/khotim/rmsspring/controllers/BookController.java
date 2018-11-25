package com.mitrais.khotim.rmsspring.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences.ResourcesType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.mitrais.khotim.rmsspring.models.Book;

@Controller(value = "bookController-client")
@RequestMapping("/books")
public class BookController {

	private static final String REMOTE_SERVICE_ROOT_URI = "http://localhost:8080/api/";

	private final RestTemplate rest;

	public BookController(RestTemplate restTemplate) {
		this.rest = restTemplate;
	}
	
	/**
	 * Show all books.
	 *
	 * @param model
	 * @return
	 * @throws URISyntaxException
	 */
	@GetMapping
	public String index(Model model) throws URISyntaxException {
		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);
		Resources<Resource<Book>> resources = client
			.follow("books")
			.toObject(new ResourcesType<Resource<Book>>(){});

		List<Book> books = resources.getContent().stream()
				.map(r -> r.getContent()).collect(Collectors.toList());
		
		model.addAttribute("books", books);
		model.addAttribute("book", new Book());

		return "book/index";
	}
	
	/**
	 * Creates a new book.
	 *
	 * @param book
	 * @return
	 * @throws URISyntaxException
	 */
	@PostMapping
	public String create(@ModelAttribute Book book) throws URISyntaxException {
		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);
		Link booksLink = client
			.follow("books")
			.asLink();

		this.rest.postForEntity(booksLink.expand().getHref(), book, Book.class);

		return "redirect:/books";
	}
	
	/**
	 * Updates an existing book.
	 *
	 * @param book
	 * @return
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/{id}/update", method = {RequestMethod.GET, RequestMethod.POST})
	public String update(@ModelAttribute Book newBook,
			Model model,
			@PathVariable final Long id,
			HttpServletRequest request) throws URISyntaxException
	{
		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);
		if (request.getMethod() == RequestMethod.POST.toString()) {
			Link booksLink = client.follow("books").asLink();

			this.rest.put(booksLink.expand().getHref(), newBook, Book.class);

			return "redirect:/books";
		}
		
		Resources<Resource<Book>> resources = client
				.follow("books")
				.toObject(new ResourcesType<Resource<Book>>(){});
		List<Book> books = resources.getContent().stream()
				.map(r -> r.getContent()).collect(Collectors.toList());
		
		Map<String, Object> params = new HashMap<>();
	    params.put("id", id);
	    
	    Book resource = client
				.follow("books")
				.follow("$._embedded.bookList[0]._links.self.href")
				.toObject(Book.class);

		model.addAttribute("books", books);
		model.addAttribute("book", resource);
		System.out.println(resource);
		
		return "book/index";
	}
}
