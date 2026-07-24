package com.patrimoine.controller;

import com.patrimoine.model.Site;
import com.patrimoine.model.TypeMedia;
import com.patrimoine.model.TypeSite;
import com.patrimoine.service.AvisService;
import com.patrimoine.service.SiteService;
import com.patrimoine.service.VisiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sites")
public class SiteController {

    private final SiteService siteService;
    private final VisiteService visiteService;
    private final AvisService avisService;

    // ---- READ (liste + recherche) ----
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("sites", siteService.listerTous());
        return "sites/list";
    }

    @GetMapping("/recherche")
    public String recherche(@RequestParam(required = false) String ville,
                             @RequestParam(required = false) TypeSite type,
                             Model model) {
        model.addAttribute("sites", siteService.rechercher(ville, type));
        model.addAttribute("villeRecherchee", ville);
        model.addAttribute("typeRecherche", type);
        model.addAttribute("types", TypeSite.values());
        return "sites/list";
    }

    @GetMapping("/proximite")
    public String parProximite(@RequestParam double latitude,
                                @RequestParam double longitude,
                                @RequestParam(defaultValue = "10") double rayon,
                                Model model) {
        model.addAttribute("sites", siteService.rechercherParProximite(latitude, longitude, rayon));
        model.addAttribute("rechercheProximite", true);
        return "sites/list";
    }

    // ---- READ (détail) ----
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Site site = siteService.trouverParId(id);
        model.addAttribute("site", site);
        model.addAttribute("visites", visiteService.listerToutes().stream()
                .filter(v -> v.getSite().getId().equals(id)).toList());
        model.addAttribute("avisListe", avisService.avisDuSite(site));
        model.addAttribute("typesMedia", TypeMedia.values());
        return "sites/detail";
    }

    // ---- CREATE ----
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("site", new Site());
        model.addAttribute("types", TypeSite.values());
        return "sites/form";
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute("site") Site site, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", TypeSite.values());
            return "sites/form";
        }
        Site cree = siteService.creer(site);
        return "redirect:/sites/" + cree.getId();
    }

    // ---- UPDATE ----
    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("site", siteService.trouverParId(id));
        model.addAttribute("types", TypeSite.values());
        return "sites/form";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, @Valid @ModelAttribute("site") Site site,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", TypeSite.values());
            return "sites/form";
        }
        siteService.modifier(id, site);
        return "redirect:/sites/" + id;
    }

    // ---- DELETE ----
    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        siteService.supprimer(id);
        return "redirect:/sites";
    }

    // ---- Upload de média (photo/vidéo) ----
    @PostMapping("/{id}/medias")
    public String ajouterMedia(@PathVariable Long id,
                                @RequestParam("fichier") MultipartFile fichier,
                                @RequestParam("type") TypeMedia type) throws IOException {
        if (!fichier.isEmpty()) {
            siteService.ajouterMedia(id, fichier, type);
        }
        return "redirect:/sites/" + id;
    }
}
