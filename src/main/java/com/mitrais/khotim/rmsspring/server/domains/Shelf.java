package com.mitrais.khotim.rmsspring.server.domains;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelf_id")
    private Long id;

    @NotNull
    private String name;

    @Min(value = 0)
    @Column(name = "maxCapacity")
    private int maxCapacity = 0;

    @Min(value = 0)
    @Column(name = "currentCapacity")
    private int currentCapacity = 0;

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    public Shelf(@NotNull String name, @Min(value = 0) int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setShelf(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setShelf(null);
    }
}
