package com.formation.service;

import com.formation.dto.FormationCreateDTO;
import com.formation.dto.FormationUpdateDTO;
import com.formation.entity.Formation;
import com.formation.entity.FormationStatut;
import com.formation.entity.Inscription;
import com.formation.entity.InscriptionStatut;
import com.formation.entity.Role;
import com.formation.entity.User;
import com.formation.exception.CannotDeleteFormationException;
import com.formation.exception.FormationNotFoundException;
import com.formation.exception.InvalidFormationDataException;
import com.formation.exception.InvalidFormationDatesException;
import com.formation.repository.FormationRepository;
import com.formation.repository.InscriptionRepository;
import com.formation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormationService {

    private final FormationRepository formationRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public Formation createFormation(FormationCreateDTO dto) {
        validateFormationData(dto.getDateDebut(), dto.getDateFin(), dto.getCapaciteMax(), dto.getPrix());

        // ✅ Formateurs optionnels à la création
        Set<User> formateurs = resolveFormateurs(dto.getFormateurIds(), true);

        Formation formation = Formation.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .duree(dto.getDuree())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .capaciteMax(dto.getCapaciteMax())
                .prix(dto.getPrix())
                .statut(dto.getStatut())
                .capaciteActuelle(0)
                .formateurs(new HashSet<>(formateurs))
                .build();

        Formation saved = formationRepository.save(formation);
        formateurs.forEach(formateur -> emailService.sendFormationAssignmentEmail(formateur, saved));

        log.info("Formation créée avec succès id={} titre={}", saved.getId(), saved.getTitre());
        return saved;
    }

    @Transactional
    public Formation updateFormation(Long id, FormationUpdateDTO dto) {
        Formation formation = getFormation(id);
        validateFormationData(dto.getDateDebut(), dto.getDateFin(), dto.getCapaciteMax(), dto.getPrix());

        if (dto.getCapaciteMax() < formation.getCapaciteActuelle()) {
            throw new InvalidFormationDataException(
                    "Données de formation invalides (capacite max inferieure aux inscriptions acceptees)"
            );
        }

        LocalDate oldDateDebut = formation.getDateDebut();
        LocalDate oldDateFin = formation.getDateFin();
        Set<User> anciensFormateurs = new HashSet<>(formation.getFormateurs());

        // Si formateurIds fournis, les valider & les utiliser; sinon garder les anciens
        Set<User> nouveauxFormateurs = (dto.getFormateurIds() == null || dto.getFormateurIds().isEmpty())
                ? anciensFormateurs
                : resolveFormateurs(dto.getFormateurIds(), false);

        formation.setTitre(dto.getTitre());
        formation.setDescription(dto.getDescription());
        formation.setDuree(dto.getDuree());
        formation.setDateDebut(dto.getDateDebut());
        formation.setDateFin(dto.getDateFin());
        formation.setCapaciteMax(dto.getCapaciteMax());
        formation.setPrix(dto.getPrix());
        formation.setStatut(dto.getStatut());
        formation.setFormateurs(nouveauxFormateurs);

        Formation updated = formationRepository.save(formation);

        Set<Long> anciensIds = anciensFormateurs.stream().map(User::getId).collect(Collectors.toSet());
        nouveauxFormateurs.stream()
                .filter(f -> !anciensIds.contains(f.getId()))
                .forEach(f -> emailService.sendFormationAssignmentEmail(f, updated));

        boolean datesChanged = !oldDateDebut.equals(updated.getDateDebut()) || !oldDateFin.equals(updated.getDateFin());
        if (datesChanged) {
            List<Inscription> inscriptionsAcceptees = inscriptionRepository.findByFormationIdAndStatut(
                    id,
                    InscriptionStatut.ACCEPTEE
            );
            inscriptionsAcceptees.forEach(i -> emailService.sendFormationModificationEmail(i.getApprenant(), updated));
        }

        log.info("Formation mise à jour id={}", updated.getId());
        return updated;
    }

    @Transactional
    public void deleteFormation(Long id) {
        Formation formation = getFormation(id);
        if (inscriptionRepository.existsByFormationId(id)) {
            throw new CannotDeleteFormationException("Impossible de supprimer une formation avec des inscrits");
        }
        formationRepository.delete(formation);
        log.info("Formation supprimée id={}", id);
    }

    @Transactional(readOnly = true)
    public Formation getFormation(Long id) {
        return formationRepository.findById(id)
                .orElseThrow(() -> new FormationNotFoundException("Formation non trouvée"));
    }

    @Transactional(readOnly = true)
    public Page<Formation> getAllFormations(Pageable pageable) {
        return formationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Formation> getFormationsByStatut(FormationStatut statut, Pageable pageable) {
        return formationRepository.findByStatut(statut, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Formation> getFormationsByFormateur(Long formateurId, Pageable pageable) {
        return formationRepository.findByFormateurId(formateurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Formation> getCatalogueFormations(
            FormationStatut statut,
            LocalDate dateDebutMin,
            LocalDate dateFinMax,
            BigDecimal prixMin,
            BigDecimal prixMax,
            Integer capaciteMin,
            Pageable pageable
    ) {
        List<FormationStatut> statuts = statut == null
                ? List.of(FormationStatut.PLANIFIEE, FormationStatut.EN_COURS)
                : List.of(statut);

        return formationRepository.findCatalogue(
                statuts,
                dateDebutMin,
                dateFinMax,
                prixMin,
                prixMax,
                capaciteMin,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<Formation> searchFormations(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return formationRepository.findAll(pageable);
        }
        return formationRepository.searchByKeyword(keyword.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public boolean isFormateurAssignedToFormation(Long formateurId, Long formationId) {
        Formation formation = getFormation(formationId);
        return formation.getFormateurs().stream().anyMatch(f -> f.getId().equals(formateurId));
    }

    private void validateFormationData(LocalDate dateDebut, LocalDate dateFin, Integer capaciteMax, BigDecimal prix) {
        if (dateDebut == null || dateFin == null || !dateFin.isAfter(dateDebut)) {
            throw new InvalidFormationDatesException("La date de fin doit être après la date de début");
        }

        if (capaciteMax == null || capaciteMax <= 0 || prix == null || prix.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFormationDataException("Données de formation invalides (capacité > 0, prix >= 0)");
        }
    }

    private Set<User> resolveFormateurs(Set<Long> formateurIds, boolean isCreation) {
        // Si création et pas de formateurs fournis → vide (optionnel)
        if (isCreation && (formateurIds == null || formateurIds.isEmpty())) {
            return new HashSet<>();
        }

        // Si mise à jour ou création avec formateurs fournis → validation stricte
        if (formateurIds == null || formateurIds.isEmpty()) {
            throw new InvalidFormationDataException("Données de formation invalides (au moins un formateur requis)");
        }

        Set<User> formateurs = new HashSet<>();
        for (Long formateurId : formateurIds) {
            User user = userRepository.findById(formateurId)
                    .orElseThrow(() -> new InvalidFormationDataException("Formateur introuvable : " + formateurId));
            if (user.getRole() != Role.FORMATEUR) {
                throw new InvalidFormationDataException("L'utilisateur " + formateurId + " n'est pas formateur");
            }
            formateurs.add(user);
        }

        return formateurs;
    }
}
