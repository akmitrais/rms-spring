package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.apis.LibraryController;
import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import com.mitrais.khotim.rmsspring.server.domains.ShelfResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ShelfResourceAssembler extends ResourceAssemblerSupport<Shelf, ShelfResource> {
	public ShelfResourceAssembler() {
		super(LibraryController.class, ShelfResource.class);
	}

	@Override
    public ShelfResource toResource(Shelf shelf) {
		return createResourceWithId(shelf.getId(), shelf);
	}

	@Override
	protected ShelfResource instantiateResource(Shelf shelf) {
		return new ShelfResource(shelf);
	}
}
