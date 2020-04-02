package com.example.bsep.repository;

import com.example.bsep.model.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AdminRepository  extends JpaRepository<Admin,Long>{

        Admin findOneByUsername (String username);

        @Query("select c.username from Admin c")
        List<String> findAllUsernames();
}
