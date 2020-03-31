package com.example.bsep.repository;

import com.example.bsep.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByName(String name);
}

