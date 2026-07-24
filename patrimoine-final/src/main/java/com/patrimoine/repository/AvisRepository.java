package com.patrimoine.repository;

import com.patrimoine.model.Avis;
import com.patrimoine.model.Site;
import com.patrimoine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByReservation_Visite_SiteOrderByDatePublicationDesc(Site site);
    List<Avis> findByReservation_Visite_GuideOrderByDatePublicationDesc(User guide);
}
