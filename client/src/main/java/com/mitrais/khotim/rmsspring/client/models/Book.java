package com.mitrais.khotim.rmsspring.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Book {
	@JsonProperty("bookId")
	private Long id;
	private String isbn;
	private String title;
	private String author;
	private String status;
	private String shelf;
}
