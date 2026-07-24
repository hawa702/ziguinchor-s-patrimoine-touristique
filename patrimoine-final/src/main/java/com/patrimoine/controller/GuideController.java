package com.patrimoine.controller;

import com.patrimoine.model.GuideDetails;
import com.patrimoine.model.User;
import com.patrimoine.service.AvisService;
import com.patrimoine.service.GuideDetailsService;
import com.patrimoine.service.UserService;
import com.patrimoine.service.VisiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/guide")
public class GuideController {

    private final UserService userService;
    private final GuideDetailsService guideDetailsService;
    private final VisiteService visiteService;
    private final AvisService avisService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User guide = userService.getUserConnecte(auth);
        GuideDetails fiche = guideDetailsService.trouverParUser(guide);
        model.addAttribute("guide", guide);
        model.addAttribute("fiche", fiche);
        model.addAttribute("agendaDuJour", visiteService.agendaDuJour(guide));
        model.addAttribute("prochainesVisites", visiteService.visitesDuGuide(guide));
        model.addAttribute("avisListe", avisService.avisDuGuide(guide));
        return "dashboard/guide";
    }

    @GetMapping("/profil")
    public String profil(Authentication auth, Model model) {
        User guide = userService.getUserConnecte(auth);
        model.addAttribute("guide", guide);
        model.addAttribute("fiche", guideDetailsService.trouverParUser(guide));
        return "guides/profil";
    }

    @PostMapping("/profil")
    public String modifierProfil(@RequestParam String telephone,
                                  @RequestParam String numeroLicence,
                                  @RequestParam String specialite,
                                  @RequestParam String biographie,
                                  Authentication auth, Model model) {
        User guide = userService.getUserConnecte(auth);
        guide.setTelephone(telephone);
        userService.mettreAJour(guide);

        GuideDetails fiche = guideDetailsService.trouverParUser(guide);
        fiche.setNumeroLicence(numeroLicence);
        fiche.setSpecialite(specialite);
        fiche.setBiographie(biographie);
        guideDetailsService.mettreAJour(fiche);

        model.addAttribute("succes", "Profil mis à jour");
        model.addAttribute("guide", guide);
        model.addAttribute("fiche", fiche);
        return "guides/profil";
    }

    @GetMapping("/{id}")
    public String voirGuide(@PathVariable Long id, Model model) {
        User guide = userService.trouverParId(id);
        model.addAttribute("guide", guide);
        model.addAttribute("fiche", guideDetailsService.trouverParUser(guide));
        model.addAttribute("avisListe", avisService.avisDuGuide(guide));
        return "guides/detail";
    }
}
