package com.formation.service;

import com.formation.dto.FormationConfigDTO;
import com.formation.dto.PaiementCreateDTO;
import com.formation.dto.PaiementResponseDTO;
import com.formation.dto.PaiementValidationDTO;
import com.formation.entity.*;
import com.formation.exception.*;
import com.formation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final UserRepository userRepository;
    private final FormationRepository formationRepository;
    private final FormationConfigRepository formationConfigRepository;
    private final SeanceRepository seanceRepository;

    public PaiementResponseDTO enregistrerPaiement(PaiementCreateDTO dto, Long userId) {
        // Accepte un apprenant OU un formateur comme bénéficiaire du paiement
        User apprenant = userRepository.findById(dto.getApprenantId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + dto.getApprenantId()));

        Formation formation = formationRepository.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("Formation non trouvée: " + dto.getFormationId()));

        FormationConfig config = formationConfigRepository.findByFormationId(dto.getFormationId()).orElse(null);
        if (config != null) {
            validateModeAuthorized(config, dto.getModePaiement());
        }

        validateModeSpecificFields(dto);

        Seance seance = null;
        if (dto.getModePaiement() == ModePaiement.SEANCE && dto.getSeanceId() != null) {
            seance = seanceRepository.findById(dto.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Séance non trouvée: " + dto.getSeanceId()));
        }

        BigDecimal remise = dto.getRemise() != null ? dto.getRemise() : BigDecimal.ZERO;
        BigDecimal montantNet = dto.getMontant().subtract(remise);
        if (montantNet.compareTo(BigDecimal.ZERO) < 0) {
            throw new MontantInvalidException("Le montant net ne peut pas être négatif");
        }

        User enregistrePar = userId != null ? userRepository.findById(userId).orElse(null) : null;

        Paiement paiement = Paiement.builder()
            .apprenant(apprenant)
            .formation(formation)
            .montant(dto.getMontant())
            .remise(remise)
            .montantNet(montantNet)
            .datePaiement(dto.getDatePaiement())
            .modePaiement(dto.getModePaiement())
            .statut(PaiementStatut.EN_ATTENTE)
            .seance(seance)
            .trancheNumber(dto.getTrancheNumber())
            .moisAnnuaire(dto.getMoisAnnuaire())
            .notes(dto.getNotes())
            .enregistrePar(enregistrePar)
            .build();

        paiement = paiementRepository.save(paiement);
        log.info("Paiement {} enregistré - apprenant {} formation {} mode {}",
            paiement.getId(), apprenant.getId(), formation.getId(), dto.getModePaiement());
        return toResponseDTO(paiement);
    }

    public PaiementResponseDTO validerPaiement(Long paiementId, PaiementValidationDTO dto, Long adminId) {
        Paiement paiement = findById(paiementId);
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new UnauthorizedActionException("Admin non trouvé"));

        if (dto.getStatut() == PaiementStatut.VALIDE) {
            paiement.setStatut(PaiementStatut.VALIDE);
            paiement.setDateValidation(LocalDateTime.now());
            paiement.setValidePar(admin);
        } else if (dto.getStatut() == PaiementStatut.REJETE) {
            paiement.setStatut(PaiementStatut.REJETE);
        }
        if (dto.getCommentaire() != null) paiement.setCommentaire(dto.getCommentaire());

        log.info("Paiement {} mis à jour: {}", paiementId, dto.getStatut());
        return toResponseDTO(paiementRepository.save(paiement));
    }

    public PaiementResponseDTO rejeterPaiement(Long paiementId, String raison) {
        Paiement paiement = findById(paiementId);
        paiement.setStatut(PaiementStatut.REJETE);
        paiement.setCommentaire(raison);
        log.info("Paiement {} rejeté", paiementId);
        return toResponseDTO(paiementRepository.save(paiement));
    }

    public void deletePaiement(Long paiementId) {
        Paiement paiement = findById(paiementId);
        paiementRepository.delete(paiement);
        log.info("Paiement {} supprimé", paiementId);
    }

    @Transactional(readOnly = true)
    public Page<PaiementResponseDTO> getPaiementsEnAttente(Pageable pageable) {
        return paiementRepository.findByStatut(PaiementStatut.EN_ATTENTE, pageable)
            .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> getPaiementsApprenant(Long apprenantId) {
        return paiementRepository.findByApprenantId(apprenantId).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> getPaiementsFormateur(Long formateurId) {
        // Récupérer toutes les formations du formateur
        List<Long> formationIds = formationRepository.findAll().stream()
            .filter(f -> f.getFormateurs().stream().anyMatch(u -> u.getId().equals(formateurId)))
            .map(f -> f.getId())
            .collect(Collectors.toList());

        if (formationIds.isEmpty()) return List.of();

        return paiementRepository.findByFormationIdIn(formationIds).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> getPaiementsApprenantFormation(Long apprenantId, Long formationId) {
        return paiementRepository.findByApprenantIdAndFormationId(apprenantId, formationId).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FormationConfigDTO getFormationConfig(Long formationId) {
        Formation formation = formationRepository.findById(formationId)
            .orElseThrow(() -> new RuntimeException("Formation non trouvée: " + formationId));
        FormationConfig config = formationConfigRepository.findByFormationId(formationId).orElse(null);
        return toConfigDTO(formation, config);
    }

    public FormationConfigDTO saveFormationConfig(Long formationId, FormationConfigDTO dto) {
        Formation formation = formationRepository.findById(formationId)
            .orElseThrow(() -> new RuntimeException("Formation non trouvée: " + formationId));
        FormationConfig config = formationConfigRepository.findByFormationId(formationId)
            .orElse(FormationConfig.builder().formation(formation).build());

        config.setPermetFormationComplete(dto.isPermetFormationComplete());
        config.setPermetParSeance(dto.isPermetParSeance());
        config.setPrixParSeance(dto.getPrixParSeance());
        config.setPermetParTranche(dto.isPermetParTranche());
        config.setNombreTranches(dto.getNombreTranches());
        config.setMontantsTranches(dto.getMontantsTranches());
        config.setPermetParAnnuaire(dto.isPermetParAnnuaire());
        config.setMontantMensuel(dto.getMontantMensuel());
        config.setNombreMois(dto.getNombreMois());

        return toConfigDTO(formation, formationConfigRepository.save(config));
    }

    private void validateModeAuthorized(FormationConfig config, ModePaiement mode) {
        boolean allowed = switch (mode) {
            case FORMATION -> config.getPermetFormationComplete();
            case SEANCE    -> config.getPermetParSeance();
            case TRANCHE   -> config.getPermetParTranche();
            case ANNUAIRE  -> config.getPermetParAnnuaire();
        };
        if (!allowed) {
            throw new RuntimeException("Le mode " + mode + " n'est pas autorisé pour cette formation");
        }
    }

    private void validateModeSpecificFields(PaiementCreateDTO dto) {
        if (dto.getModePaiement() == ModePaiement.TRANCHE
                && (dto.getTrancheNumber() == null || dto.getTrancheNumber() < 1 || dto.getTrancheNumber() > 9)) {
            throw new RuntimeException("Le numéro de tranche doit être entre 1 et 9");
        }
        if (dto.getModePaiement() == ModePaiement.ANNUAIRE
                && (dto.getMoisAnnuaire() == null || dto.getMoisAnnuaire() < 1 || dto.getMoisAnnuaire() > 12)) {
            throw new RuntimeException("Le mois doit être entre 1 et 12");
        }
        if (dto.getModePaiement() == ModePaiement.SEANCE && dto.getSeanceId() == null) {
            throw new RuntimeException("L'identifiant de la séance est obligatoire pour le mode SEANCE");
        }
    }

    private FormationConfigDTO toConfigDTO(Formation formation, FormationConfig config) {
        if (config == null) {
            return FormationConfigDTO.builder()
                .formationId(formation.getId())
                .formationTitre(formation.getTitre())
                .prixFormation(formation.getPrix())
                .permetFormationComplete(true)
                .permetParSeance(false)
                .permetParTranche(false)
                .permetParAnnuaire(false)
                .build();
        }
        return FormationConfigDTO.builder()
            .formationId(formation.getId())
            .formationTitre(formation.getTitre())
            .prixFormation(formation.getPrix())
            .permetFormationComplete(config.getPermetFormationComplete())
            .permetParSeance(config.getPermetParSeance())
            .prixParSeance(config.getPrixParSeance())
            .permetParTranche(config.getPermetParTranche())
            .nombreTranches(config.getNombreTranches())
            .montantsTranches(config.getMontantsTranches())
            .permetParAnnuaire(config.getPermetParAnnuaire())
            .montantMensuel(config.getMontantMensuel())
            .nombreMois(config.getNombreMois())
            .build();
    }

    private Paiement findById(Long id) {
        return paiementRepository.findById(id)
            .orElseThrow(() -> new PaiementNotFoundException("Paiement non trouvé: " + id));
    }

    private PaiementResponseDTO toResponseDTO(Paiement p) {
        return PaiementResponseDTO.builder()
            .id(p.getId())
            .apprenantId(p.getApprenant().getId())
            .apprenantNom(p.getApprenant().getNom())
            .apprenantPrenom(p.getApprenant().getPrenom())
            .formationId(p.getFormation().getId())
            .formationTitre(p.getFormation().getTitre())
            .modePaiement(p.getModePaiement())
            .montant(p.getMontant())
            .remise(p.getRemise())
            .montantNet(p.getMontantNet())
            .datePaiement(p.getDatePaiement())
            .statut(p.getStatut())
            .trancheNumber(p.getTrancheNumber())
            .seanceId(p.getSeance() != null ? p.getSeance().getId() : null)
            .seanceTitre(p.getSeance() != null ? p.getSeance().getTitre() : null)
            .moisAnnuaire(p.getMoisAnnuaire())
            .notes(p.getNotes())
            .commentaire(p.getCommentaire())
            .dateEnregistrement(p.getDateEnregistrement())
            .enregistrePar(p.getEnregistrePar() != null
                ? PaiementResponseDTO.UserInfo.builder()
                    .id(p.getEnregistrePar().getId())
                    .nom(p.getEnregistrePar().getNom())
                    .prenom(p.getEnregistrePar().getPrenom())
                    .build()
                : null)
            .dateValidation(p.getDateValidation())
            .validePar(p.getValidePar() != null
                ? PaiementResponseDTO.UserInfo.builder()
                    .id(p.getValidePar().getId())
                    .nom(p.getValidePar().getNom())
                    .prenom(p.getValidePar().getPrenom())
                    .build()
                : null)
            .build();
    }
}
