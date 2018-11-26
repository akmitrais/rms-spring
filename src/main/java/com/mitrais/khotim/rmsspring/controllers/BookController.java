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
import java.util.stream.Collectors;

@Controller(value = "bookController-client")
@RequestMapping("/books")
public class BookController {
	private static final String REMOTE_SERVICE_ROOT_URI = "http://localhost:8080/api/";
	private Traverson apiCall = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);

	private final RestTemplate rest;

	public BookController(RestTemplate restTemplate) throws URISyntaxException {
		this.rest = restTemplate;
	}
	
	/**
	 * Show all books.
	 *
	 * @param model Holder for model attributes.
	 * @return View template.
	 */
	@GetMapping
	public String index(Model model) {
		Resources<Resource<Book>> resources = apiCall
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
	 */
	@PostMapping
	public String create(@ModelAttribute Book book) {
		Link booksLink = apiCall
			.follow("books")
			.asLink();

		this.rest.postForEntity(booksLink.expand().getHref(), book, Book.class);

		return "redirect:/books";
	}

	/**
	 * Updates and existing book.
	 *
	 * @param newBook The book data which contains updated values.
	 * @param model   Holder for model attributes.
	 * @param id      The book id.
	 * @param request Provides information related to current request through the HttpServlet.
	 * @return View template.
	 */
	@RequestMapping(value = "/{id}/update", method = {RequestMethod.GET, RequestMethod.POST})
	public String update(@ModelAttribute Book newBook,
			Model model,
			@PathVariable final Long id,
			HttpServletRequest request)
	{
		List<String> booksLink = apiCall
				.follow("books")
				.toObject("$._embedded.bookList[?(@.id==" + id + ")]._links.self.href");

		if (request.getMethod().equals(RequestMethod.POST.toString())) {
			this.rest.put(booksLink.get(0), newBook, Book.class);

			return "redirect:/books";
		}
		
		Resources<Resource<Book>> resources = apiCall
				.follow("books")
				.toObject(new ResourcesType<Resource<Book>>(){});

		List<Book> books = resources.getContent().stream()
				.map(Resource::getContent)
				.collect(Collectors.toList());

		Book book = this.rest.getForObject(booksLink.get(0), Book.class);

		model.addAttribute("books", books);
		model.addAttribute("book", book);
		
		return "book/index";
	}

	/**
	 * Deletes an existing book.
	 *
	 * @param id The book id.
	 * @return Redirect to book list.
	 */
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable final Long id) {
		List<String> booksLink = apiCall
				.follow("books")
				.toObject("$._embedded.bookList[?(@.id==" + id + ")]._links.self.href");

		this.rest.delete(booksLink.get(0));

		return "redirect:/books";
	}
}
