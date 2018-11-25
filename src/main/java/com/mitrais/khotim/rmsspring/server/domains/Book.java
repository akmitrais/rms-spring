package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id", insertable = false, updatable = false)
    private Long bookId;

    @NotNull
    private String isbn;

    @NotNull
    private String title;

    @NotNull
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

    @Column(name = "shelf_id", insertable = false, updatable = false)
    private Long shelfId;

    /**
     * Gets shelfId.
     *
     * @return The value of shelfId.
     */
    public Long getShelfId() {
        if (shelf != null) {
            return shelf.getId();
        }

        return shelfId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id")
    @JsonIgnore
    private Shelf shelf;

    public Book(@NotNull String isbn, @NotNull String title, @NotNull String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
}
