package com.sell.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sell.model.Role;
import com.sell.model.User;
import com.sell.repository.RoleRepository;
import com.sell.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private BCryptPasswordEncoder passEncode;


    // Business Logic

    public Boolean saveUser(User user) {
        User existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser == null) {
            user.setPassword(passEncode.encode(user.getPassword()));
            Role adminRole = roleRepo.findByRoleName("Admin");
            List<User> adminUsers = userRepo.findByRole(adminRole);
            if (adminUsers.isEmpty()) {
                user.setRole(adminRole);
            } else {
                Role userRole = roleRepo.findByRoleName("Customer");
                user.setRole(userRole);
            }

            userRepo.save(user);
            return true;
        }
        return false;
    }


    public User checkLogIn(String email,String pw){
        User user = userRepo.findByEmail(email);
        if(user != null){
            if(passEncode.matches(pw,user.getPassword())){
                return user;
            }
        }
        return null;
    }

    @PostConstruct
    public void initRoles() {
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("ShopAdmin");
        createRoleIfNotFound("Customer");
        createRoleIfNotFound("Delivery");
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepo.findByRoleName(roleName) == null) {
            Role role = new Role(roleName);
            roleRepo.save(role);
        }
    }

}
