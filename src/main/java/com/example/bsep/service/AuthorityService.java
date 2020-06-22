package com.example.bsep.service;

import java.util.ArrayList;
import java.util.List;

import com.example.bsep.model.Role;
import com.example.bsep.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public List<Role> findById(Long id) {
        Role auth = this.authorityRepository.getOne(id);
        List<Role> auths = new ArrayList<>();
        auths.add(auth);
        return auths;
    }

    public List<Role> findByname(String name) {
        Role auth = this.authorityRepository.findByName(name);
        List<Role> auths = new ArrayList<>();
        auths.add(auth);
        return auths;
    }

}

