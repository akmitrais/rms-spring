package com.mitrais.khotim.rmsspring.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.khotim.rmsspring.server.domains.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatusIgnoreCase(String status);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByTitleContainingIgnoreCaseAndStatusIgnoreCase(String title, String status);
}
