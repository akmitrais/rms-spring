package com.mitrais.khotim.rmsspring.server.repositories;

import com.mitrais.khotim.rmsspring.server.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
