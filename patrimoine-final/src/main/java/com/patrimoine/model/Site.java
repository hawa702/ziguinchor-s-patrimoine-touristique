package com.patrimoine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Site (id, nom, description, adresse, latitude, longitude, ville, horaires, capacite_accueil)
 */
@Entity
@Table(name = "sites")
@Data
@NoArgsConstructor
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Column(length = 2000)
    private String description;

    private String adresse;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    /** Champ ajoute en plus du modele de base, necessaire pour le CRUD monuments/parcs/musees */
    @Enumerated(EnumType.STRING)
    private TypeSite type;

    // ---- Geolocalisation ----
    private Double latitude;
    private Double longitude;

    /** Horaires d'ouverture en texte libre, ex : "Lun-Ven 9h-18h, Sam 9h-13h" */
    private String horaires;

    /** Capacite d'accueil globale du site */
    private Integer capaciteAccueil;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> medias = new ArrayList<>();

    /** Note moyenne recalculee a chaque nouvel avis lie a ce site (voir AvisService) */
    private Double noteMoyenne = 0.0;
    private Integer nombreAvis = 0;
}
