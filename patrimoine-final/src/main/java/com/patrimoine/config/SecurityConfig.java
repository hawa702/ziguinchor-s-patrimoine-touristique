package com.patrimoine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /** Redirige l'user vers son tableau de bord en fonction de son rôle après connexion */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String target = switch (role) {
                case "ROLE_ADMIN" -> "/conservateur/dashboard";
                case "ROLE_GUIDE" -> "/guide/dashboard";
                default -> "/touriste/dashboard";
            };
            response.sendRedirect(target);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Ressources publiques
                .requestMatchers("/", "/accueil", "/css/**", "/uploads/**", "/webjars/**").permitAll()
                .requestMatchers("/inscription/**", "/login", "/h2-console/**").permitAll()
                // Consultation des sites : publique (touristes non connectés peuvent parcourir)
                .requestMatchers("/sites", "/sites/{id:[0-9]+}", "/sites/recherche", "/sites/proximite").permitAll()

                // Espace conservateur
                .requestMatchers("/conservateur/**").hasRole("ADMIN")
                // Gestion des sites (création/modif/suppression/médias) : conservateur uniquement
                .requestMatchers("/sites/nouveau", "/sites/*/modifier", "/sites/*/supprimer", "/sites/*/medias").hasRole("ADMIN")
                // Planification de visites : conservateur
                .requestMatchers("/visites/nouvelle", "/visites/*/modifier", "/visites/*/supprimer").hasRole("ADMIN")

                // Fiche publique d'un guide (consultable par tous les users connectés)
                .requestMatchers("/guide/{id:[0-9]+}").authenticated()
                // Espace guide (dashboard, profil personnel)
                .requestMatchers("/guide/**").hasRole("GUIDE")
                .requestMatchers("/avis/*/repondre").hasRole("GUIDE")

                // Espace touriste
                .requestMatchers("/touriste/**", "/reservations/**").hasRole("TOURIST")

                // Avis : dépôt réservé aux touristes connectés, suppression réservée au conservateur (modération)
                .requestMatchers("/avis/nouveau/**").hasRole("TOURIST")
                .requestMatchers("/avis/*/supprimer").hasRole("ADMIN")
                .requestMatchers("/avis/**").authenticated()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .cacheControl(cache -> cache.disable())
        )// pour la console H2
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .exceptionHandling(ex -> ex.accessDeniedPage("/acces-refuse"));

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }
}
