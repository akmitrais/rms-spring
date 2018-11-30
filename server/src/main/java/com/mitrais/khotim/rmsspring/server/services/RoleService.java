package com.mitrais.khotim.rmsspring.server.services;

import com.mitrais.khotim.rmsspring.server.assemblers.RoleResourceAssembler;
import com.mitrais.khotim.rmsspring.server.domains.Role;
import com.mitrais.khotim.rmsspring.server.domains.RoleName;
import com.mitrais.khotim.rmsspring.server.domains.RoleResource;
import com.mitrais.khotim.rmsspring.server.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository repository;
    private final RoleResourceAssembler assembler;

    @Autowired
    public RoleService(RoleRepository repository, RoleResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public List<RoleResource> findAll() {
        return assembler.toResources(repository.findAll());
    }

    public RoleResource toResource(Role role) {
        return assembler.toResource(role);
    }

    public Optional<Role> findById(Long id) {
        return repository.findById(id);
    }

    public RoleResource save(Role role) {
        return assembler.toResource(repository.save(role));
    }

    public boolean deleteById(Long id) {
        repository.deleteById(id);

        return true;
    }

    public Optional<Role> findByName(RoleName name) {
        return repository.findByName(name);
    }
}
