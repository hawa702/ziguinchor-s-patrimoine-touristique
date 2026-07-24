package com.patrimoine.service;

import com.patrimoine.model.RoleType;
import com.patrimoine.model.User;
import com.patrimoine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GuideDetailsService guideDetailsService;
    private final PasswordEncoder passwordEncoder;

    public User inscrireTouriste(User u) {
        u.setRole(RoleType.TOURIST);
        u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
        return userRepository.save(u);
    }

    /** Cree le User + sa fiche GuideDetails (non verifiee tant qu'un ADMIN ne l'a pas validee) */
    public User inscrireGuide(User u) {
        u.setRole(RoleType.GUIDE);
        u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
        User cree = userRepository.save(u);
        guideDetailsService.creerFiche(cree);
        return cree;
    }

    public boolean emailExiste(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> guidesEnAttente() {
        return guideDetailsService.guidesEnAttente();
    }

    public List<User> guidesValides() {
        return guideDetailsService.guidesValides();
    }

    public void validerGuide(Long userId) {
        guideDetailsService.validerGuide(userId);
    }

    public void refuserGuide(Long userId) {
        userRepository.deleteById(userId);
    }

    public User trouverParId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }

    public User getUserConnecte(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }

    public User mettreAJour(User u) {
        return userRepository.save(u);
    }
}
