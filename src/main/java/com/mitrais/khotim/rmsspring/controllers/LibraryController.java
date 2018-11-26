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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

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
}
