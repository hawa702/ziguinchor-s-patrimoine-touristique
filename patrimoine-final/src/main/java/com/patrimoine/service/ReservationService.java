package com.patrimoine.service;

import com.patrimoine.model.Reservation;
import com.patrimoine.model.StatutReservation;
import com.patrimoine.model.User;
import com.patrimoine.model.Visite;
import com.patrimoine.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VisiteService visiteService;

    /**
     * Cree une reservation (1 place) en verifiant le stock disponible sur la visite.
     * La reservation est confirmee immediatement (pas d'etat intermediaire).
     */
    public Reservation reserver(Long visiteId, User touriste) {
        Visite visite = visiteService.trouverParId(visiteId);

        if (visite.getPlacesDisponibles() == null || visite.getPlacesDisponibles() < 1) {
            throw new IllegalStateException("Plus de places disponibles pour cette visite");
        }

        Reservation reservation = new Reservation();
        reservation.setVisite(visite);
        reservation.setTouriste(touriste);
        reservation.setStatut(StatutReservation.CONFIRME);
        reservation.setQrCodeHash(UUID.randomUUID().toString());

        visite.setPlacesDisponibles(visite.getPlacesDisponibles() - 1);

        return reservationRepository.save(reservation);
    }

    /** Annulation : libere la place sur la visite */
    public Reservation annuler(Long reservationId) {
        Reservation r = trouverParId(reservationId);
        if (r.getStatut() == StatutReservation.ANNULE) {
            return r;
        }
        r.setStatut(StatutReservation.ANNULE);

        Visite visite = r.getVisite();
        visite.setPlacesDisponibles(visite.getPlacesDisponibles() + 1);

        return reservationRepository.save(r);
    }

    public Reservation trouverParId(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation introuvable : " + id));
    }

    public List<Reservation> reservationsDuTouriste(User touriste) {
        return reservationRepository.findByTouriste(touriste);
    }
}
