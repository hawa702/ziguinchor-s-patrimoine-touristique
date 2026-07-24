package com.patrimoine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Visite (id, site_id, guide_id, date_heure_debut, duree, places_max, places_disponibles)
 */
@Entity
@Table(name = "visites")
@Data
@NoArgsConstructor
public class Visite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private User guide;

    private LocalDateTime dateHeureDebut;

    /** Duree en minutes */
    private Integer duree;

    private Integer placesMax;

    /** Places encore reservables, decremente/incremente par ReservationService */
    private Integer placesDisponibles;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "visite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
