package com.formation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesApprenantDTO {
    private Long apprenantId;
    private String nom;
    private String prenom;
    private String email;
    
    private Long formationId;
    private String formationTitre;
    
    private int totalSeances;
    private int seancesAssistees;
    private double tauxPresence;
    private double moyenneGenerale;
    
    private List<NoteResponseDTO> detailNotes;
    private List<PresenceResponseDTO> detailPresences;
}
