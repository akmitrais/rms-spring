package com.mitrais.khotim.rmsspring.server.repositories;

import com.mitrais.khotim.rmsspring.server.domains.Role;
import com.mitrais.khotim.rmsspring.server.domains.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
