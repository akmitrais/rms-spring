package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.hateoas.ResourceSupport;

/**
 * Class defined for exposing book resource as JSON.
 * 
 * @author Khotim
 */
@JsonPropertyOrder({"bookId", "isbn", "title", "author", "status", "shelfId"})
public class BookResource extends ResourceSupport {
	@JsonIgnore
	private final Book book;
	
	public BookResource(Book book) {
		this.book = book;
	}

	/**
     * Gets book id.
     * 
	 * @return The value of book id or throw exception if not book found.
	 */
	@JsonProperty("bookId")
	public Long getBookId() {
		if (this.book.getId() == null) {
			throw new RuntimeException("Couldn't find any book.");
		}
		
		return this.book.getId();
	}
	
	/**
	 * Gets book ISBN.
	 * 
	 * @return The value of book ISBN.
	 */
	public String getIsbn() {
		return this.book.getIsbn();
	}
	
	/**
	 * Gets book title.
	 * 
	 * @return The value of book title.
	 */
	public String getTitle() {
		return this.book.getTitle();
	}
	
	/**
	 * Gets book author.
	 * 
	 * @return The value of book author.
	 */
	public String getAuthor() {
		return this.book.getAuthor();
	}
	
	/**
	 * Gets book status.
	 * 
	 * @return The value of book status.
	 */
	public String getStatus() {
		return this.book.getStatus();
	}
	
	/**
	 * Gets shelf name.
	 * 
	 * @return The name of shelf name
	 */
	public String getShelfId() {
		return this.book.getShelf().getName();
	}
}
