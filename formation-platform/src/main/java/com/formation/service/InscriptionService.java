package com.formation.service;

import com.formation.entity.Formation;
import com.formation.entity.FormationStatut;
import com.formation.entity.Inscription;
import com.formation.entity.InscriptionStatut;
import com.formation.entity.Role;
import com.formation.entity.User;
import com.formation.exception.InscriptionAlreadyExistsException;
import com.formation.exception.InscriptionNotFoundException;
import com.formation.exception.NoAvailablePlacesException;
import com.formation.exception.UnauthorizedActionException;
import com.formation.repository.InscriptionRepository;
import com.formation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final FormationService formationService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public Inscription inscrireApprenant(Long formationId, Long apprenantId) {
        Formation formation = formationService.getFormation(formationId);
        User apprenant = getApprenant(apprenantId);

        if (formation.getStatut() == FormationStatut.TERMINEE) {
            throw new UnauthorizedActionException("Inscription impossible sur une formation terminee");
        }

        if (apprenant.getDateCreation() == null) {
            throw new UnauthorizedActionException("Un apprenant ne peut s'inscrire qu'après sa création de compte");
        }

        if (inscriptionRepository.existsByFormationIdAndApprenantId(formationId, apprenantId)) {
            throw new InscriptionAlreadyExistsException("Vous êtes déjà inscrit à cette formation");
        }

        if (!formation.isPlacesDisponibles()) {
            throw new NoAvailablePlacesException("Aucune place disponible pour cette formation");
        }

        Inscription inscription = Inscription.builder()
                .formation(formation)
                .apprenant(apprenant)
                .statut(InscriptionStatut.EN_ATTENTE)
                .build();

        Inscription saved = inscriptionRepository.save(inscription);

        emailService.sendInscriptionConfirmationEmail(apprenant, formation);
        userRepository.findByRole(Role.ADMIN)
                .forEach(admin -> emailService.sendAdminNewInscriptionNotification(admin, saved, formation));

        log.info("Demande d'inscription créée id={} formation={} apprenant={}", saved.getId(), formationId, apprenantId);
        return saved;
    }

    @Transactional
    public Inscription accepterInscription(Long inscriptionId) {
        Inscription inscription = getInscription(inscriptionId);

        if (inscription.getStatut() != InscriptionStatut.EN_ATTENTE) {
            throw new UnauthorizedActionException("Seules les inscriptions en attente peuvent être acceptées");
        }

        Formation formation = inscription.getFormation();
        if (!formation.isPlacesDisponibles()) {
            throw new NoAvailablePlacesException("Aucune place disponible pour cette formation");
        }

        inscription.setStatut(InscriptionStatut.ACCEPTEE);
        inscription.setDateAcceptation(LocalDateTime.now());
        inscription.setDateRejet(null);
        inscription.setMotifRejet(null);

        formation.setCapaciteActuelle(formation.getCapaciteActuelle() + 1);

        Inscription updated = inscriptionRepository.save(inscription);
        emailService.sendInscriptionAcceptanceEmail(updated.getApprenant(), updated.getFormation());

        log.info("Inscription acceptée id={}", inscriptionId);
        return updated;
    }

    @Transactional
    public Inscription rejeterInscription(Long inscriptionId, String motif) {
        Inscription inscription = getInscription(inscriptionId);

        if (inscription.getStatut() != InscriptionStatut.EN_ATTENTE) {
            throw new UnauthorizedActionException("Seules les inscriptions en attente peuvent être rejetées");
        }

        inscription.setStatut(InscriptionStatut.REJETEE);
        inscription.setDateRejet(LocalDateTime.now());
        inscription.setMotifRejet(motif);
        inscription.setDateAcceptation(null);

        Inscription updated = inscriptionRepository.save(inscription);
        emailService.sendInscriptionRejectionEmail(updated.getApprenant(), updated.getFormation(), motif);

        log.info("Inscription rejetée id={}", inscriptionId);
        return updated;
    }

    @Transactional(readOnly = true)
    public Page<Inscription> getInscriptionsEnAttente(Pageable pageable) {
        return inscriptionRepository.findByStatut(InscriptionStatut.EN_ATTENTE, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Inscription> getInscriptionsParFormation(Long formationId, Pageable pageable) {
        return inscriptionRepository.findByFormationId(formationId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Inscription> getInscriptionsAccepteesParFormation(Long formationId, Pageable pageable) {
        return inscriptionRepository.findByFormationIdAndStatut(formationId, InscriptionStatut.ACCEPTEE, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Inscription> getInscriptionsApprenant(Long apprenantId, Pageable pageable) {
        return inscriptionRepository.findByApprenantId(apprenantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Inscription> getInscriptionsAccepteesFormateur(Long formateurId, Pageable pageable) {
        return inscriptionRepository.findByFormationFormateursIdAndStatut(formateurId, InscriptionStatut.ACCEPTEE, pageable);
    }

    @Transactional
    public void annulerInscription(Long inscriptionId, Long apprenantId) {
        Inscription inscription = getInscription(inscriptionId);

        if (!inscription.getApprenant().getId().equals(apprenantId)) {
            throw new UnauthorizedActionException("Action non autorisée");
        }

        if (inscription.getStatut() == InscriptionStatut.ACCEPTEE) {
            Formation formation = inscription.getFormation();
            int current = formation.getCapaciteActuelle() == null ? 0 : formation.getCapaciteActuelle();
            formation.setCapaciteActuelle(Math.max(current - 1, 0));
        }

        inscription.setStatut(InscriptionStatut.ANNULEE);
        inscription.setDateAcceptation(null);
        inscription.setDateRejet(null);
        inscription.setMotifRejet(null);

        inscriptionRepository.save(inscription);
        log.info("Inscription annulée id={} par apprenant={}", inscriptionId, apprenantId);
    }

    @Transactional(readOnly = true)
    public Inscription getInscription(Long inscriptionId) {
        return inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new InscriptionNotFoundException("Inscription non trouvée"));
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsAccepteesByFormation(Long formationId) {
        return inscriptionRepository.findByFormationIdAndStatut(formationId, InscriptionStatut.ACCEPTEE);
    }

    private User getApprenant(Long apprenantId) {
        User user = userRepository.findById(apprenantId)
                .orElseThrow(() -> new UnauthorizedActionException("Apprenant introuvable"));
        if (user.getRole() != Role.APPRENANT) {
            throw new UnauthorizedActionException("Action non autorisée");
        }
        return user;
    }
}
