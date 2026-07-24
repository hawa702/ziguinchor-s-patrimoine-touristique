package com.patrimoine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Avis (id, reservation_id, note, commentaire, date_publication)
 * Un avis porte sur une reservation terminee : le site ET le guide de cette
 * visite recoivent tous les deux la note (voir AvisService).
 */
@Entity
@Table(name = "avis")
@Data
@NoArgsConstructor
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Min(1)
    @Max(5)
    private Integer note;

    @Column(length = 1000)
    private String commentaire;

    private LocalDateTime datePublication = LocalDateTime.now();

    /** Reponse du guide au commentaire (fonctionnalite complementaire) */
    @Column(length = 1000)
    private String reponse;
}
