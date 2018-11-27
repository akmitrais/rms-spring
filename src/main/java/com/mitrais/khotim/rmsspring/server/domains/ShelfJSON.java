package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.util.List;

@Value
@JsonPropertyOrder({"shelfId", "name", "maxCapacity", "currentCapacity", "books"})
public class ShelfJSON {
	private final Shelf shelf;
	
    /**
     * Gets shelf id.
     * 
	 * @return The value of shelf id.
	 */
	@JsonProperty("shelfId")
	public Long getId() {
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
