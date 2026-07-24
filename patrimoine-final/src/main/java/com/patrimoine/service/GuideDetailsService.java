package com.patrimoine.service;

import com.patrimoine.model.GuideDetails;
import com.patrimoine.model.RoleType;
import com.patrimoine.model.User;
import com.patrimoine.repository.GuideDetailsRepository;
import com.patrimoine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideDetailsService {

    private final GuideDetailsRepository guideDetailsRepository;
    private final UserRepository userRepository;

    public void creerFiche(User guide) {
        GuideDetails fiche = new GuideDetails();
        fiche.setUser(guide);
        fiche.setEstVerifie(false);
        guideDetailsRepository.save(fiche);
    }

    public GuideDetails trouverParUser(User user) {
        return guideDetailsRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Fiche guide introuvable pour cet utilisateur"));
    }

    public List<User> guidesEnAttente() {
        return guideDetailsRepository.findByEstVerifie(false).stream()
                .map(GuideDetails::getUser)
                .toList();
    }

    public List<User> guidesValides() {
        return guideDetailsRepository.findByEstVerifie(true).stream()
                .map(GuideDetails::getUser)
                .toList();
    }

    public void validerGuide(Long userId) {
        User guide = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Guide introuvable"));
        GuideDetails fiche = trouverParUser(guide);
        fiche.setEstVerifie(true);
        guideDetailsRepository.save(fiche);
    }

    public GuideDetails mettreAJour(GuideDetails fiche) {
        return guideDetailsRepository.save(fiche);
    }

    /** Recalcule la note moyenne du guide a partir de tous ses avis (voir AvisService) */
    public void recalculerNoteMoyenne(User guide, double moyenne) {
        GuideDetails fiche = trouverParUser(guide);
        fiche.setNoteMoyenne(Math.round(moyenne * 10.0) / 10.0);
        guideDetailsRepository.save(fiche);
    }
}
