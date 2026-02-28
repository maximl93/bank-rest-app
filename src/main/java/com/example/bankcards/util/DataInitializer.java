package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        setDefaultRoles();
        setDefaultAdminUser();
    }

    public void setDefaultRoles() {
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            UserRole adminRole = new UserRole();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

        }

        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            UserRole userRole = new UserRole();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
    }

    public void setDefaultAdminUser() {
        if (userRepository.findByEmail("admin1@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin1@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin1"));
            UserRole adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("Роль ADMIN нет в базе данных"));
            admin.setRole(adminRole);
            userRepository.save(admin);
        }
    }
}
