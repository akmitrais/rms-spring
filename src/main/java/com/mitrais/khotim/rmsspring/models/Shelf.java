package com.mitrais.khotim.rmsspring.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Shelf {
	@JsonProperty("shelfId")
	private Long id;
	private String name;
	private String maxCapacity;
	private String currentCapacity;
	private List<Book> books;
}
