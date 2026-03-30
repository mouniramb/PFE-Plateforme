package com.formation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envoie les identifiants au formateur nouvellement créé.
     */
    public void sendFormateurCredentials(String toEmail, String nom, String prenom,
                                         String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🎓 Bienvenue sur la Plateforme de Formation - Vos identifiants");
            message.setText(buildFormateurEmailBody(nom, prenom, toEmail, password));

            mailSender.send(message);
            log.info("Email envoyé avec succès à : {}", toEmail);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {} : {}", toEmail, e.getMessage());
            // On ne bloque pas le flux principal en cas d'échec email
        }
    }

    private String buildFormateurEmailBody(String nom, String prenom,
                                           String email, String password) {
        return String.format("""
                Bonjour %s %s,
                
                Votre compte formateur a été créé sur la Plateforme de Formation.
                
                ──────────────────────────────────
                  VOS IDENTIFIANTS DE CONNEXION
                ──────────────────────────────────
                  Email     : %s
                  Mot de passe : %s
                ──────────────────────────────────
                
                Pour vous connecter, rendez-vous sur :
                http://localhost:4200/login
                
                ⚠️  Pour des raisons de sécurité, nous vous conseillons de changer
                votre mot de passe dès votre première connexion.
                
                Cordialement,
                L'équipe de la Plateforme de Formation
                """,
                prenom, nom, email, password);
    }
}