package com.mitrais.khotim.rmsspring.server.assemblers;

import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.BookResource;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class BookResourceAssembler extends ResourceAssemblerSupport<Book, BookResource> {
	public BookResourceAssembler(Class<?> controllerClass, Class<BookResource> resourceType) {
		super(controllerClass, resourceType);
	}
	
	@Override
	public BookResource toResource(Book entity) {
		BookResource resource = createResourceWithId(entity.getId(), entity);
		return resource;
	}
}
