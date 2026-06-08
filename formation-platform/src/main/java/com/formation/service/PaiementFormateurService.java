package com.formation.service;

import com.formation.dto.PaiementFormateurCreateDTO;
import com.formation.dto.PaiementFormateurResponseDTO;
import com.formation.entity.*;
import com.formation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaiementFormateurService {

    private final PaiementFormateurRepository paiementFormateurRepository;
    private final UserRepository userRepository;
    private final FormationRepository formationRepository;

    public PaiementFormateurResponseDTO enregistrer(PaiementFormateurCreateDTO dto, Long adminId) {
        User formateur = userRepository.findById(dto.getFormateurId())
            .orElseThrow(() -> new RuntimeException("Formateur non trouvé: " + dto.getFormateurId()));

        Formation formation = formationRepository.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("Formation non trouvée: " + dto.getFormationId()));

        User admin = adminId != null ? userRepository.findById(adminId).orElse(null) : null;

        BigDecimal montantParTranche = null;
        if (dto.getModePaiement() == ModePaiement.TRANCHE && dto.getNombreTranches() != null
                && dto.getNombreTranches() > 0) {
            montantParTranche = dto.getMontant()
                .divide(BigDecimal.valueOf(dto.getNombreTranches()), 2, RoundingMode.HALF_UP);
        }

        PaiementFormateur p = PaiementFormateur.builder()
            .formateur(formateur)
            .formation(formation)
            .montant(dto.getMontant())
            .modePaiement(dto.getModePaiement())
            .nombreTranches(dto.getNombreTranches())
            .montantParTranche(montantParTranche)
            .numeroTranche(dto.getNumeroTranche())
            .datePaiement(dto.getDatePaiement())
            .statut(PaiementStatut.VALIDE)
            .notes(dto.getNotes())
            .enregistrePar(admin)
            .build();

        p = paiementFormateurRepository.save(p);
        log.info("Paiement formateur {} enregistré — formateur {} formation {}",
            p.getId(), formateur.getId(), formation.getId());
        return toDTO(p);
    }

    @Transactional(readOnly = true)
    public List<PaiementFormateurResponseDTO> getByFormateur(Long formateurId) {
        return paiementFormateurRepository.findByFormateurId(formateurId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaiementFormateurResponseDTO> getAll() {
        return paiementFormateurRepository.findAll()
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private PaiementFormateurResponseDTO toDTO(PaiementFormateur p) {
        return PaiementFormateurResponseDTO.builder()
            .id(p.getId())
            .formateurId(p.getFormateur().getId())
            .formateurNom(p.getFormateur().getNom())
            .formateurPrenom(p.getFormateur().getPrenom())
            .formationId(p.getFormation().getId())
            .formationTitre(p.getFormation().getTitre())
            .montant(p.getMontant())
            .montantParTranche(p.getMontantParTranche())
            .modePaiement(p.getModePaiement())
            .nombreTranches(p.getNombreTranches())
            .numeroTranche(p.getNumeroTranche())
            .datePaiement(p.getDatePaiement())
            .statut(p.getStatut())
            .notes(p.getNotes())
            .dateCreation(p.getDateCreation())
            .build();
    }
}
