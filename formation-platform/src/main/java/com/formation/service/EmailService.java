package com.formation.service;

import com.formation.entity.Formation;
import com.formation.entity.Inscription;
import com.formation.entity.Paiement;
import com.formation.entity.User;
import com.formation.entity.Seance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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
        sendEmail(
            toEmail,
            "Bienvenue sur la Plateforme de Formation - Vos identifiants",
            buildFormateurEmailBody(nom, prenom, toEmail, password)
        );
            log.info("Email envoyé avec succès à : {}", toEmail);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {} : {}", toEmail, e.getMessage());
            // On ne bloque pas le flux principal en cas d'échec email
        }
    }

    public void sendFormationAssignmentEmail(User formateur, Formation formation) {
      String body = String.format("""
          Bonjour %s %s,

          Vous avez été affecté à la formation suivante :

          Titre : %s
          Dates : du %s au %s
          Nombre d'apprenants attendus : %d

          Cordialement,
          Plateforme de Formation
          """,
          formateur.getPrenom(),
          formateur.getNom(),
          formation.getTitre(),
          formation.getDateDebut(),
          formation.getDateFin(),
          formation.getCapaciteMax());
      sendEmail(formateur.getEmail(), "Affectation a une formation", body);
    }

    public void sendInscriptionConfirmationEmail(User apprenant, Formation formation) {
      String body = String.format("""
          Bonjour %s %s,

          Votre demande d'inscription a bien ete enregistree pour la formation : %s.
          Statut actuel : EN_ATTENTE.

          Cordialement,
          Plateforme de Formation
          """, apprenant.getPrenom(), apprenant.getNom(), formation.getTitre());
      sendEmail(apprenant.getEmail(), "Confirmation de demande d'inscription", body);
    }

    public void sendInscriptionAcceptanceEmail(User apprenant, Formation formation) {
      String body = String.format("""
          Bonjour %s %s,

          Votre inscription a ete ACCEPTEE pour la formation : %s.
          Dates : du %s au %s

          Cordialement,
          Plateforme de Formation
          """, apprenant.getPrenom(), apprenant.getNom(), formation.getTitre(), formation.getDateDebut(), formation.getDateFin());
      sendEmail(apprenant.getEmail(), "Inscription acceptee", body);
    }

    public void sendInscriptionRejectionEmail(User apprenant, Formation formation, String motif) {
      String body = String.format("""
          Bonjour %s %s,

          Votre inscription a ete REJETEE pour la formation : %s.
          Motif : %s

          Cordialement,
          Plateforme de Formation
          """, apprenant.getPrenom(), apprenant.getNom(), formation.getTitre(), motif);
      sendEmail(apprenant.getEmail(), "Inscription rejetee", body);
    }

    public void sendFormationModificationEmail(User apprenant, Formation formation) {
      String body = String.format("""
          Bonjour %s %s,

          La formation "%s" a ete modifiee.
          Nouvelles dates : du %s au %s

          Cordialement,
          Plateforme de Formation
          """, apprenant.getPrenom(), apprenant.getNom(), formation.getTitre(), formation.getDateDebut(), formation.getDateFin());
      sendEmail(apprenant.getEmail(), "Modification de formation", body);
    }

    public void sendAdminNewInscriptionNotification(User admin, Inscription inscription, Formation formation) {
      String dateDemande = inscription.getDateInscription() != null
          ? inscription.getDateInscription().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
          : "N/A";
      String body = String.format("""
          Bonjour %s %s,

          Une nouvelle demande d'inscription est en attente de validation.

          Formation : %s
          Apprenant : %s %s (%s)
          Date de demande : %s

          Cordialement,
          Plateforme de Formation
          """,
          admin.getPrenom(),
          admin.getNom(),
          formation.getTitre(),
          inscription.getApprenant().getPrenom(),
          inscription.getApprenant().getNom(),
          inscription.getApprenant().getEmail(),
          dateDemande);
      sendEmail(admin.getEmail(), "Nouvelle demande d'inscription", body);
    }

    // Sprint 3 - Seance Management Methods
    public void sendSeanceAssignmentEmail(User formateur, Seance seance) {
      String dateHeure = seance.getDateHeureDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
      String salleInfo = seance.getSalle() != null ? seance.getSalle().getNom() : "À confirmer";
      
      String body = String.format("""
          Bonjour %s %s,

          Vous avez été affecté à une nouvelle séance.

          Titre : %s
          Formation : %s
          Date et heure : %s
          Salle : %s
          Durée : %d minutes

          Cordialement,
          Plateforme de Formation
          """,
          formateur.getPrenom(),
          formateur.getNom(),
          seance.getTitre(),
          seance.getFormation().getTitre(),
          dateHeure,
          salleInfo,
          seance.getDureeMinutes());
      sendEmail(formateur.getEmail(), "Nouvelle séance assignée", body);
    }

    public void sendSeanceModificationEmail(User formateur, Seance seance) {
      String dateHeure = seance.getDateHeureDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
      String salleInfo = seance.getSalle() != null ? seance.getSalle().getNom() : "À confirmer";
      
      String body = String.format("""
          Bonjour %s %s,

          Une séance a été modifiée.

          Titre : %s
          Formation : %s
          Nouvelle date et heure : %s
          Salle : %s
          Durée : %d minutes

          Cordialement,
          Plateforme de Formation
          """,
          formateur.getPrenom(),
          formateur.getNom(),
          seance.getTitre(),
          seance.getFormation().getTitre(),
          dateHeure,
          salleInfo,
          seance.getDureeMinutes());
      sendEmail(formateur.getEmail(), "Modification de séance", body);
    }

    public void sendSeanceAnnulationEmail(User formateur, Seance seance) {
      String dateHeure = seance.getDateHeureDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
      
      String body = String.format("""
          Bonjour %s %s,

          La séance suivante a été annulée.

          Titre : %s
          Formation : %s
          Date et heure prévue : %s

          Cordialement,
          Plateforme de Formation
          """,
          formateur.getPrenom(),
          formateur.getNom(),
          seance.getTitre(),
          seance.getFormation().getTitre(),
          dateHeure);
      sendEmail(formateur.getEmail(), "Annulation de séance", body);
    }

    public void sendPaiementConfirmationEmail(User apprenant, Paiement paiement) {
        String formationTitre = paiement.getFormation() != null ? paiement.getFormation().getTitre() : "N/A";
        String body = String.format("""
            Bonjour %s %s,

            Votre paiement a bien été enregistré et est en attente de validation.

            Formation        : %s
            Mode             : %s
            Montant payé     : %.2f DT
            Date de paiement : %s

            Cordialement,
            Plateforme de Formation
            """,
            apprenant.getPrenom(), apprenant.getNom(),
            formationTitre,
            paiement.getModePaiement(),
            paiement.getMontantNet() != null ? paiement.getMontantNet() : paiement.getMontant(),
            paiement.getDatePaiement());
        sendEmail(apprenant.getEmail(), "Paiement enregistré - " + formationTitre, body);
    }

    public void sendPaiementValidationEmail(User apprenant, Paiement paiement) {
        String formationTitre = paiement.getFormation() != null ? paiement.getFormation().getTitre() : "N/A";
        String body = String.format("""
            Bonjour %s %s,

            Votre paiement a été validé par notre équipe.

            Formation        : %s
            Mode             : %s
            Montant validé   : %.2f DT
            Date de paiement : %s
            Statut           : VALIDÉ

            Cordialement,
            Plateforme de Formation
            """,
            apprenant.getPrenom(), apprenant.getNom(),
            formationTitre,
            paiement.getModePaiement(),
            paiement.getMontantNet() != null ? paiement.getMontantNet() : paiement.getMontant(),
            paiement.getDatePaiement());
        sendEmail(apprenant.getEmail(), "Paiement validé - " + formationTitre, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
      try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Email envoye avec succes a : {}", toEmail);
      } catch (Exception e) {
        log.error("Erreur lors de l'envoi de l'email a {} : {}", toEmail, e.getMessage());
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

                Cordialement,
                L'équipe de la Plateforme de Formation
                """,
                prenom, nom, email, password);
    }
}