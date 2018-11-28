package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    public static final String SHELVED = "shelved";
    public static final String NOT_SHELVED = "not_shelved";

    @Pattern(
        regexp = SHELVED + "|" + NOT_SHELVED,
        flags = Pattern.Flag.CASE_INSENSITIVE
    )
    private String status = NOT_SHELVED;
    
    /**
     * Sets status field.
     */
    public void setStatus(String status) {
    	this.status = status == null ? NOT_SHELVED : status.toLowerCase();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id")
    @JsonIgnore
    private Shelf shelf;

    public Book(@NotBlank String isbn, @NotBlank String title, @NotBlank String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
}
