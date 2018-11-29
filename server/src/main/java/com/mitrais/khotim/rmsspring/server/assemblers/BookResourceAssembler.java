package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.apis.BookController;
import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.BookResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class BookResourceAssembler extends ResourceAssemblerSupport<Book, BookResource> {
	public BookResourceAssembler() {
		super(BookController.class, BookResource.class);
	}
	
	@Override
	public BookResource toResource(Book entity) {
		return createResourceWithId(entity.getId(), entity);
	}

	@Override
	protected BookResource instantiateResource(Book entity) {
		return new BookResource(entity);
	}
}
