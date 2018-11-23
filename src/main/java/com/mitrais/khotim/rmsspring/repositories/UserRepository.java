package com.mitrais.khotim.rmsspring.repositories;

import com.mitrais.khotim.rmsspring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
