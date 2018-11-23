package com.mitrais.khotim.rmsspring.repositories;

import com.mitrais.khotim.rmsspring.domains.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}
