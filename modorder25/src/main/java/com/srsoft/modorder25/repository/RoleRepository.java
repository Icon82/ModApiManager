package com.srsoft.modorder25.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.srsoft.modorder25.entity.Role;

import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}