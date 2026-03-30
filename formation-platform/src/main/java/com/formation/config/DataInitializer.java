package com.formation.config;

import com.formation.entity.Role;
import com.formation.entity.User;
import com.formation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer l'administrateur par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("admin@formation.com")) {
            User admin = User.builder()
                    .nom("Admin")
                    .prenom("Super")
                    .email("admin@formation.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("✅ Administrateur par défaut créé : admin@formation.com / Admin@123");
        }
    }
}