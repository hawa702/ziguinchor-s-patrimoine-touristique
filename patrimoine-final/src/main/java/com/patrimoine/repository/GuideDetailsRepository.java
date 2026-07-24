package com.patrimoine.repository;

import com.patrimoine.model.GuideDetails;
import com.patrimoine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuideDetailsRepository extends JpaRepository<GuideDetails, Long> {
    Optional<GuideDetails> findByUser(User user);
    List<GuideDetails> findByEstVerifie(boolean estVerifie);
}
