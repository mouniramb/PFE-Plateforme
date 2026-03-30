package com.formation.dto;

import com.formation.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long   id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String dateCreation;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole().name())
                .dateCreation(user.getDateCreation() != null
                        ? user.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "")
                .build();
    }
}