package com.mitrais.khotim.rmsspring.server.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Min(value = 0)
    @Column(name = "maxCapacity")
    private int maxCapacity = 0;

    @Min(value = 0)
    @Column(name = "currentCapacity")
    private int currentCapacity = 0;

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    public Shelf(@NotBlank String name, @Min(value = 0) int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
    }
}
