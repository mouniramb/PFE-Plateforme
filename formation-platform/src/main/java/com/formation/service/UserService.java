package com.formation.service;

import com.formation.dto.CreateFormateurRequest;
import com.formation.dto.RegisterRequest;
import com.formation.dto.UserResponse;
import com.formation.entity.Role;
import com.formation.entity.User;
import com.formation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";

    // ── UserDetailsService ───────────────────────────────────────────────────

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));
    }

    // ── Inscription apprenant ────────────────────────────────────────────────

    @Transactional
    public UserResponse registerApprenant(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.APPRENANT)
                .build();

        User saved = userRepository.save(user);
        log.info("Nouvel apprenant inscrit : {}", saved.getEmail());
        return UserResponse.fromUser(saved);
    }

    // ── CRUD Formateurs ──────────────────────────────────────────────────────

    @Transactional
    public UserResponse createFormateur(CreateFormateurRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email.");
        }

        // Générer mot de passe temporaire
        String rawPassword = generatePassword(10);

        User formateur = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.FORMATEUR)
                .build();

        User saved = userRepository.save(formateur);

        // Envoyer email avec identifiants
        emailService.sendFormateurCredentials(
            saved.getEmail(), saved.getNom(), saved.getPrenom(), rawPassword);

        log.info("Formateur créé : {}", saved.getEmail());
        return UserResponse.fromUser(saved);
    }

    @Transactional
    public UserResponse updateFormateur(Long id, CreateFormateurRequest request) {
        User formateur = getFormateurById(id);

        // Vérifier email unique si modifié
        if (!formateur.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        formateur.setNom(request.getNom());
        formateur.setPrenom(request.getPrenom());
        formateur.setEmail(request.getEmail());

        User updated = userRepository.save(formateur);
        log.info("Formateur mis à jour : {}", updated.getEmail());
        return UserResponse.fromUser(updated);
    }

    @Transactional
    public void deleteFormateur(Long id) {
        User formateur = getFormateurById(id);
        userRepository.delete(formateur);
        log.info("Formateur supprimé : {}", formateur.getEmail());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllFormateurs() {
        return userRepository.findByRole(Role.FORMATEUR)
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getFormateurResponseById(Long id) {
        return UserResponse.fromUser(getFormateurById(id));
    }

    // ── Utilitaires ─────────────────────────────────────────────────────────

    private User getFormateurById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur non trouvé avec l'id : " + id));
        if (user.getRole() != Role.FORMATEUR) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un formateur.");
        }
        return user;
    }

    private String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}