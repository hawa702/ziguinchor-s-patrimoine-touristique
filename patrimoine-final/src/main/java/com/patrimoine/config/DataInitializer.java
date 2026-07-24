package com.patrimoine.config;

import com.patrimoine.model.GuideDetails;
import com.patrimoine.model.RoleType;
import com.patrimoine.model.User;
import com.patrimoine.repository.GuideDetailsRepository;
import com.patrimoine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cree automatiquement des comptes de demonstration au premier demarrage,
 * pour pouvoir tester l'application sans passer par l'inscription.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GuideDetailsRepository guideDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        creerUtilisateur("conservateur@patrimoine.sn", "Conservateur", "Principal", RoleType.ADMIN);
        User guide = creerUtilisateur("guide@patrimoine.sn", "Guide", "Demo", RoleType.GUIDE);
        creerUtilisateur("touriste@patrimoine.sn", "Touriste", "Demo", RoleType.TOURIST);

        // La fiche GuideDetails du guide de demo est deja verifiee pour pouvoir tester direct
        if (guide != null && guideDetailsRepository.findByUser(guide).isEmpty()) {
            GuideDetails fiche = new GuideDetails();
            fiche.setUser(guide);
            fiche.setEstVerifie(true);
            fiche.setSpecialite("Histoire & culture locale");
            fiche.setBiographie("Guide passionne par la Casamance.");
            guideDetailsRepository.save(fiche);
        }
    }

    private User creerUtilisateur(String email, String prenom, String nom, RoleType role) {
        if (userRepository.existsByEmail(email)) return null;

        User u = new User();
        u.setEmail(email);
        u.setPrenom(prenom);
        u.setNom(nom);
        u.setPasswordHash(passwordEncoder.encode("password123"));
        u.setRole(role);
        return userRepository.save(u);
    }
}
