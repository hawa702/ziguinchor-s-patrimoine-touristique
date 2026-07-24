package com.patrimoine.service;

import com.patrimoine.model.User;
import com.patrimoine.model.Visite;
import com.patrimoine.repository.VisiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisiteService {

    private final VisiteRepository visiteRepository;

    public List<Visite> listerToutes() {
        return visiteRepository.findByDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(LocalDateTime.now());
    }

    public Visite trouverParId(Long id) {
        return visiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visite introuvable : " + id));
    }

    /** places_disponibles demarre egal a places_max a la creation */
    public Visite planifier(Visite visite) {
        visite.setPlacesDisponibles(visite.getPlacesMax());
        return visiteRepository.save(visite);
    }

    public Visite modifier(Long id, Visite donnees) {
        Visite visite = trouverParId(id);
        visite.setSite(donnees.getSite());
        visite.setGuide(donnees.getGuide());
        visite.setDateHeureDebut(donnees.getDateHeureDebut());
        visite.setDuree(donnees.getDuree());
        visite.setDescription(donnees.getDescription());
        // Si la capacite max change, on ajuste les places disponibles en gardant le nombre deja reserve
        int reserve = visite.getPlacesMax() - visite.getPlacesDisponibles();
        visite.setPlacesMax(donnees.getPlacesMax());
        visite.setPlacesDisponibles(Math.max(0, donnees.getPlacesMax() - reserve));
        return visiteRepository.save(visite);
    }

    public void supprimer(Long id) {
        visiteRepository.deleteById(id);
    }

    /** Agenda personnel du jour pour un guide */
    public List<Visite> agendaDuJour(User guide) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(LocalTime.MAX);
        return visiteRepository.findByGuideAndDateHeureDebutBetween(guide, debut, fin);
    }

    public List<Visite> visitesDuGuide(User guide) {
        return visiteRepository.findByGuide(guide);
    }
}
