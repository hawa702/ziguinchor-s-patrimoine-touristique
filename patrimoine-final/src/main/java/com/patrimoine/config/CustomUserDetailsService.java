package com.patrimoine.config;

import com.patrimoine.model.GuideDetails;
import com.patrimoine.model.RoleType;
import com.patrimoine.repository.GuideDetailsRepository;
import com.patrimoine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final GuideDetailsRepository guideDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.patrimoine.model.User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));

        // Un compte guide non encore verifie par un ADMIN ne peut pas se connecter
        boolean actif = true;
        if (u.getRole() == RoleType.GUIDE) {
            actif = guideDetailsRepository.findByUser(u)
                    .map(GuideDetails::isEstVerifie)
                    .orElse(false);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getEmail())
                .password(u.getPasswordHash())
                .disabled(!actif)
                .authorities(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
                .build();
    }
}
