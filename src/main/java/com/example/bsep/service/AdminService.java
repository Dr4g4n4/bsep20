package com.example.bsep.service;

import com.example.bsep.model.Admin;
import com.example.bsep.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin findOneByUserName(String username) {
        return adminRepository.findOneByUsername(username);
    }
}
