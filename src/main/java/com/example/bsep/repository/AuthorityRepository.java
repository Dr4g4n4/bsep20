package com.example.bsep.repository;

import com.example.bsep.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorityRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}

