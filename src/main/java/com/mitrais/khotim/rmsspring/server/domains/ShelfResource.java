package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import java.util.List;

@JsonPropertyOrder({"shelfId", "name", "maxCapacity", "currentCapacity", "books"})
public class ShelfResource extends Resource<Shelf> {
	@JsonIgnore
	private final Shelf shelf;

	public ShelfResource(Shelf shelf, Link... links) {
		super(shelf, links);
		this.shelf = shelf;
	}

	/**
     * Gets shelf id.
     * 
	 * @return The value of shelf id.
	 */
	public Long getShelfId() {
		if (this.shelf.getId() == null) {
			throw new RuntimeException("Couldn't find any shelf.");
		}
		
		return this.shelf.getId();
	}
	
	/**
	 * Gets shelf name.
	 * 
	 * @return The value of shelf name.
	 */
	public String getName() {
		return this.shelf.getName();
	}
	
	/**
	 * Gets shelf maximum capacity.
	 * 
	 * @return The value of shelf maximum capacity.
	 */
	public int getMaxCapacity() {
		return this.shelf.getMaxCapacity();
	}
	
	/**
	 * Gets shelf current capacity.
	 * 
	 * @return The value of shelf current capacity. 
	 */
	public int getCurrentCapacity() {
		return this.shelf.getCurrentCapacity();
	}
	
	/**
	 * Gets all books within shelf.
	 * 
	 * @return All books in shelf.
	 */
	public List<Book> getBooks() {
		return this.shelf.getBooks();
	}
}
