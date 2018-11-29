package com.mitrais.khotim.rmsspring.server.repositories;

import com.mitrais.khotim.rmsspring.server.domains.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}
