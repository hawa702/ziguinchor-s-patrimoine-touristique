package com.patrimoine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GuideDetails (id_user, numero_licence, biographie, note_moyenne, est_verifie)
 * Informations complémentaires propres aux comptes guides.
 * Relation 1-1 avec User : chaque guide a exactement une fiche GuideDetails.
 */
@Entity
@Table(name = "guide_details")
@Data
@NoArgsConstructor
public class GuideDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_user", unique = true)
    private User user;

    private String numeroLicence;

    @Column(length = 2000)
    private String biographie;

    /** Champ pratique en plus du modèle de base */
    private String specialite;

    /** Recalculée automatiquement à chaque nouvel avis lié à ce guide */
    private Double noteMoyenne = 0.0;

    /** Un guide non vérifié ne peut pas se connecter (validation par un ADMIN) */
    private boolean estVerifie = false;
}
