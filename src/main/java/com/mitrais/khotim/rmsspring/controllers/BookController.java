package com.mitrais.khotim.rmsspring.controllers;

import com.mitrais.khotim.rmsspring.models.Book;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences.ResourcesType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
	 * @param model Holder for model attributes.
	 * @return View template.
	 * @throws URISyntaxException Exception when string couldn't be passed as URI reference.
	 */
	@GetMapping
	public String index(Model model) throws URISyntaxException {
		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);
		Resources<Resource<Book>> resources = client
			.follow("books")
			.toObject(new ResourcesType<Resource<Book>>(){});

		List<Book> books = resources.getContent().stream()
				.map(Resource::getContent).collect(Collectors.toList());
		
		model.addAttribute("books", books);
		model.addAttribute("book", new Book());

		return "book/index";
	}
	
	/**
	 * Creates a new book.
	 *
	 * @param book The book data.
	 * @return View template.
	 * @throws URISyntaxException Exception when string couldn't be passed as URI reference.
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
	 * @param newBook The book data.
	 * @return View template.
	 * @throws URISyntaxException Exception when string couldn't be passed as URI reference.
	 */
	@RequestMapping(value = "/{id}/update", method = {RequestMethod.GET, RequestMethod.POST})
	public String update(@ModelAttribute Book newBook,
			Model model,
			@PathVariable final Long id,
			HttpServletRequest request) throws URISyntaxException
	{
		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);

		if (request.getMethod().equals(RequestMethod.POST.toString())) {
			List<String> booksLink = client
					.follow("books")
					.toObject("$._embedded.bookList[?(@.id==" + id + ")]._links.self.href");

			this.rest.put(booksLink.get(0), newBook, Book.class);

			return "redirect:/books";
		}
		
		Resources<Resource<Book>> resources = client
				.follow("books")
				.toObject(new ResourcesType<Resource<Book>>(){});

		List<Book> books = resources.getContent().stream()
				.map(Resource::getContent)
				.collect(Collectors.toList());

		Book book = new Book();
		for (Book b: books) {
			if (b.getId().equals(id)) {
				book = b;
				break;
			}
		}

		model.addAttribute("books", books);
		model.addAttribute("book", book);
		System.out.println(book);
		
		return "book/index";
	}
}
