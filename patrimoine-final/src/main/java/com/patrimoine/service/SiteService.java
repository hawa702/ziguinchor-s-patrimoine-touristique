package com.patrimoine.service;

import com.patrimoine.model.Media;
import com.patrimoine.model.Site;
import com.patrimoine.model.TypeMedia;
import com.patrimoine.model.TypeSite;
import com.patrimoine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final FileStorageService fileStorageService;

    public List<Site> listerTous() {
        return siteRepository.findAll();
    }

    public Site trouverParId(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Site introuvable : " + id));
    }

    public Site creer(Site site) {
        return siteRepository.save(site);
    }

    public Site modifier(Long id, Site donnees) {
        Site site = trouverParId(id);
        site.setNom(donnees.getNom());
        site.setDescription(donnees.getDescription());
        site.setAdresse(donnees.getAdresse());
        site.setVille(donnees.getVille());
        site.setType(donnees.getType());
        site.setLatitude(donnees.getLatitude());
        site.setLongitude(donnees.getLongitude());
        site.setHoraires(donnees.getHoraires());
        site.setCapaciteAccueil(donnees.getCapaciteAccueil());
        return siteRepository.save(site);
    }

    public void supprimer(Long id) {
        siteRepository.deleteById(id);
    }

    public List<Site> rechercher(String ville, TypeSite type) {
        return siteRepository.rechercher(
                (ville == null || ville.isBlank()) ? null : ville,
                type
        );
    }

    /** Recherche des sites dans un rayon (en km) autour d'un point GPS, triés du plus proche au plus loin */
    public List<Site> rechercherParProximite(double latitude, double longitude, double rayonKm) {
        return siteRepository.findAll().stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .filter(s -> distanceKm(latitude, longitude, s.getLatitude(), s.getLongitude()) <= rayonKm)
                .sorted((a, b) -> Double.compare(
                        distanceKm(latitude, longitude, a.getLatitude(), a.getLongitude()),
                        distanceKm(latitude, longitude, b.getLatitude(), b.getLongitude())))
                .toList();
    }

    /** Formule de Haversine : distance en kilomètres entre deux points GPS */
    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double rayonTerre = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return rayonTerre * c;
    }

    public void ajouterMedia(Long siteId, MultipartFile fichier, TypeMedia type) throws IOException {
        Site site = trouverParId(siteId);
        String chemin = fileStorageService.sauvegarder(fichier);

        Media media = new Media();
        media.setUrl(chemin);
        media.setType(type);
        media.setSite(site);

        site.getMedias().add(media);
        siteRepository.save(site);
    }
}
