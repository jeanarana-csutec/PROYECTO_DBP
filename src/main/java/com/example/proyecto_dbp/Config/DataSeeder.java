package com.example.proyecto_dbp.Config;

import com.cloudinary.provisioning.Account;
import com.example.proyecto_dbp.User.Rol;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@mail.com").isEmpty()) {

            User admin = new User();
            admin.setNombre("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            userRepository.save(admin);
        }
    }
}