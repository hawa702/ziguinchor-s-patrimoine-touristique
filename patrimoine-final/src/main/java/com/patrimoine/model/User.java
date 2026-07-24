package com.patrimoine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User (id, nom, email, password_hash, role [TOURIST, GUIDE, ADMIN])
 * Represente un utilisateur de la plateforme, quel que soit son role.
 * Les informations specifiques aux guides (numero de licence, biographie,
 * note moyenne, verification) sont dans l'entite separee GuideDetails.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    /** Champ pratique en plus du modele de base, non contradictoire avec le schema */
    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String passwordHash;

    private String telephone;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
