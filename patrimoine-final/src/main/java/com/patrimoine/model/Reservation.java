package com.patrimoine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Reservation (id, touriste_id, visite_id, statut [CONFIRME, ANNULE], qr_code_hash)
 * Chaque reservation correspond a 1 place sur une visite.
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "touriste_id")
    private User touriste;

    @ManyToOne
    @JoinColumn(name = "visite_id")
    private Visite visite;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.CONFIRME;

    /** Hash unique encode dans le QR code du ticket */
    @Column(unique = true)
    private String qrCodeHash;

    private LocalDateTime dateReservation = LocalDateTime.now();
}
