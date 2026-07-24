package com.patrimoine.repository;

import com.patrimoine.model.Reservation;
import com.patrimoine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByTouriste(User touriste);
    Optional<Reservation> findByQrCodeHash(String qrCodeHash);
}
