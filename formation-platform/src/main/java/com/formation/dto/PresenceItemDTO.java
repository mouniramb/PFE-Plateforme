package com.formation.dto;

import com.formation.entity.PresenceStatut;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceItemDTO {
    @NotNull(message = "L'apprenant est obligatoire")
    private Long apprenantId;

    @NotNull(message = "Le statut est obligatoire")
    private PresenceStatut statut;

    private String commentaire;
}
