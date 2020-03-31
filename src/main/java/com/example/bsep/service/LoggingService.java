package com.example.bsep.service;

import java.util.List;

import com.example.bsep.model.Admin;
import com.example.bsep.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoggingService implements UserDetailsService {

    @Autowired
    private AdminRepository userRepo;

    @Autowired
    private AuthorityService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin loginUser(Admin u) {
        Admin user = (Admin) loadUserByUsername(u.getUsername());
        if (user == null)
            return null;
        else if (user != null && user.isFirstLogin())  {
            user.setFirstLogin(false);
            return user;
        }
        else
            return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Admin user = userRepo.findAdminByUsername(username);

        return user;

    }

}
