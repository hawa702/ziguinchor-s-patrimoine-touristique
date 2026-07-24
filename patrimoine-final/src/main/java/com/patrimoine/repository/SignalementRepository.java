package com.patrimoine.repository;

import com.patrimoine.model.Signalement;
import com.patrimoine.model.StatutSignalement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    List<Signalement> findByStatut(StatutSignalement statut);
}
