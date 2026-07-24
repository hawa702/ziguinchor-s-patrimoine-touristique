package com.patrimoine.service;

import com.patrimoine.model.Avis;
import com.patrimoine.model.Reservation;
import com.patrimoine.model.Site;
import com.patrimoine.model.User;
import com.patrimoine.repository.AvisRepository;
import com.patrimoine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;
    private final SiteRepository siteRepository;
    private final GuideDetailsService guideDetailsService;

    /**
     * Depose un avis sur une reservation terminee. La note s'applique a la fois
     * au site et au guide de la visite reservee (recalcul des deux moyennes).
     */
    public Avis deposerAvis(Reservation reservation, int note, String commentaire) {
        Avis avis = new Avis();
        avis.setReservation(reservation);
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        Avis sauvegarde = avisRepository.save(avis);

        Site site = reservation.getVisite().getSite();
        recalculerNoteMoyenneSite(site);

        User guide = reservation.getVisite().getGuide();
        if (guide != null) {
            recalculerNoteMoyenneGuide(guide);
        }

        return sauvegarde;
    }

    private void recalculerNoteMoyenneSite(Site site) {
        List<Avis> tousLesAvis = avisRepository.findByReservation_Visite_SiteOrderByDatePublicationDesc(site);
        double moyenne = tousLesAvis.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        site.setNoteMoyenne(Math.round(moyenne * 10.0) / 10.0);
        site.setNombreAvis(tousLesAvis.size());
        siteRepository.save(site);
    }

    private void recalculerNoteMoyenneGuide(User guide) {
        List<Avis> avisGuide = avisRepository.findByReservation_Visite_GuideOrderByDatePublicationDesc(guide);
        double moyenne = avisGuide.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        guideDetailsService.recalculerNoteMoyenne(guide, moyenne);
    }

    public List<Avis> avisDuSite(Site site) {
        return avisRepository.findByReservation_Visite_SiteOrderByDatePublicationDesc(site);
    }

    public List<Avis> avisDuGuide(User guide) {
        return avisRepository.findByReservation_Visite_GuideOrderByDatePublicationDesc(guide);
    }

    public void repondre(Long avisId, String reponse) {
        Avis avis = avisRepository.findById(avisId)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable"));
        avis.setReponse(reponse);
        avisRepository.save(avis);
    }

    public Avis trouverParId(Long id) {
        return avisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable"));
    }

    public void supprimer(Long avisId) {
        avisRepository.deleteById(avisId);
    }
}
