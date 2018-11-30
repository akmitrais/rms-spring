package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.UserResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.User;
import com.mitrais.khotim.rmsspring.server.domains.UserResource;
import com.mitrais.khotim.rmsspring.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserResourceAssembler assembler;

    @Autowired
    public UserService(UserRepository repository, UserResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public List<UserResource> findAll() {
        return assembler.toResources(repository.findAll());
    }

    public UserResource toResource(User user) {
        return assembler.toResource(user);
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public UserResource save(User user) {
        return assembler.toResource(repository.save(user));
    }

    public boolean deleteById(Long id) {
        repository.deleteById(id);

        return true;
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
