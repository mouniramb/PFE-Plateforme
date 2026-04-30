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
        log.info("🚀 Initialisation des données par défaut...");
        
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
            log.info("✅ Administrateur créé : admin@formation.com / Admin@123");
        }

        // Créer un apprenant par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("apprenant1@test.com")) {
            User apprenant = User.builder()
                    .nom("Dupont")
                    .prenom("Jean")
                    .email("apprenant1@test.com")
                    .password(passwordEncoder.encode("Apprenant@123"))
                    .role(Role.APPRENANT)
                    .build();
            userRepository.save(apprenant);
            log.info("✅ Apprenant créé : apprenant1@test.com / Apprenant@123");
        }

        // Créer un formateur par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("formateur1@test.com")) {
            User formateur = User.builder()
                    .nom("Martin")
                    .prenom("Pierre")
                    .email("formateur1@test.com")
                    .password(passwordEncoder.encode("Formateur@123"))
                    .role(Role.FORMATEUR)
                    .build();
            userRepository.save(formateur);
            log.info("✅ Formateur créé : formateur1@test.com / Formateur@123");
        }
        
        log.info("✅ Initialisation des données terminée!");
    }
}