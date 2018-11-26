package com.mitrais.khotim.rmsspring.controllers;

import com.mitrais.khotim.rmsspring.models.Shelf;
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

@Controller(value = "libraryController-client")
@RequestMapping("/libraries")
public class LibraryController {
	private static final String REMOTE_SERVICE_ROOT_URI = "http://localhost:8080/api/";
	private Traverson apiCall = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);

	private final RestTemplate rest;

	public LibraryController(RestTemplate restTemplate) throws URISyntaxException {
		this.rest = restTemplate;
	}
	
	/**
	 * Show all libraries.
	 *
	 * @param model Holder for model attributes.
	 * @return View template.
	 */
	@GetMapping
	public String index(Model model) {
		Resources<Resource<Shelf>> resources = apiCall
			.follow("libraries")
			.toObject(new ResourcesType<Resource<Shelf>>(){});

		List<Shelf> shelves = resources.getContent().stream()
				.map(Resource::getContent).collect(Collectors.toList());
		
		model.addAttribute("shelves", shelves);
		model.addAttribute("shelf", new Shelf());

		return "library/index";
	}

	/**
	 * Creates a new shelf.
	 *
	 * @param shelf The shelf data.
	 * @return View template.
	 */
	@PostMapping
	public String create(@ModelAttribute Shelf shelf) {
		Link shelfLink = apiCall
				.follow("libraries")
				.asLink();

		this.rest.postForEntity(shelfLink.expand().getHref(), shelf, Shelf.class);

		return "redirect:/libraries";
	}

	/**
	 *
	 * @param newShelf The shelf data which contains updated values.
	 * @param model   Holder for model attributes.
	 * @param id      The shelf id.
	 * @param request Provides information related to current request through the HttpServlet.
	 * @return View template.
	 */
	@RequestMapping(value = "/{id}/update", method = {RequestMethod.GET, RequestMethod.POST})
	public String update(
			@ModelAttribute Shelf newShelf,
			Model model,
			@PathVariable final Long id,
			HttpServletRequest request
	) {
		List<String> shelfLink = apiCall
				.follow("libraries")
				.toObject("$._embedded.shelfList[?(@.id==" + id + ")]._links.self.href");

		if (request.getMethod().equals(RequestMethod.POST.toString())) {
			this.rest.put(shelfLink.get(0), newShelf, Shelf.class);

			return "redirect:/libraries";
		}

		Resources<Resource<Shelf>> resources = apiCall
				.follow("libraries")
				.toObject(new ResourcesType<Resource<Shelf>>(){});

		List<Shelf> shelves = resources.getContent().stream()
				.map(Resource::getContent)
				.collect(Collectors.toList());

		Shelf shelf = this.rest.getForObject(shelfLink.get(0), Shelf.class);

		model.addAttribute("shelves", shelves);
		model.addAttribute("shelf", shelf);

		return "library/index";
	}
}
