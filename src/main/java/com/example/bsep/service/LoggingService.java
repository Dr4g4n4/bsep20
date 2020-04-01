package com.example.bsep.service;

import java.util.HashSet;
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

    public Admin loginUser(String username, String password) {
        if (!validateFields(username, password)) {
            return null;
        }

        Admin user = (Admin) loadUserByUsername(username);

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

    private boolean validateFields(String username, String password ) {
        return validateUsername(username) && validatePass(password);
    }

    private boolean validateUsername(String username) {
        List<String> allUsernames = userRepo.findAllUsernames();
        if (username.equals("")) {
            return false;
        }

        for (String u : allUsernames) {
            if (u.equals(username)) {
                return false;
            }
        }

        return true;
    }

    private boolean validatePass(String pass) {
        boolean ret = false;
        boolean numCheck = false;
        boolean upperCheck = false;
        boolean lowerCheck = false;

        if (pass.length() < 8) {
           return ret;
        }else {
            for (Character c : pass.toCharArray()) {
                if (Character.isDigit(c)) {
                    numCheck = true;
                    continue;
                } else if (Character.isLowerCase(c)) {
                    lowerCheck = true;
                    continue;
                } else if (Character.isUpperCase(c)) {
                    upperCheck = true;
                    continue;
                }
            }

            if (!numCheck || !lowerCheck || !upperCheck) {
                ret = false;
            } else {
                ret = true;
            }
        }

        return ret;
    }

}
