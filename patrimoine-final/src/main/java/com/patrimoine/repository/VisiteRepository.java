package com.patrimoine.repository;

import com.patrimoine.model.User;
import com.patrimoine.model.Visite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisiteRepository extends JpaRepository<Visite, Long> {
    List<Visite> findBySiteId(Long siteId);
    List<Visite> findByGuide(User guide);
    List<Visite> findByGuideAndDateHeureDebutBetween(User guide, LocalDateTime debut, LocalDateTime fin);
    List<Visite> findByDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(LocalDateTime maintenant);
}
