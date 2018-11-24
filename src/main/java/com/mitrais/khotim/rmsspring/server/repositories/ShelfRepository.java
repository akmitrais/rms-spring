package com.mitrais.khotim.rmsspring.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.khotim.rmsspring.server.domains.Shelf;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}
