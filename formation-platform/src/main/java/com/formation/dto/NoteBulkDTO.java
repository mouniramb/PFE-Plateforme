package com.formation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteBulkDTO {
    @NotNull(message = "La séance est obligatoire")
    private Long seanceId;

    @NotEmpty(message = "Au moins une note est obligatoire")
    private List<NoteItemDTO> notes;
}
