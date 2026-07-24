package com.patrimoine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signalement (id, avis_id, motif, statut [EN_ATTENTE, RESOLU])
 */
@Entity
@Table(name = "signalements")
@Data
@NoArgsConstructor
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "avis_id")
    private Avis avis;

    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutSignalement statut = StatutSignalement.EN_ATTENTE;
}
