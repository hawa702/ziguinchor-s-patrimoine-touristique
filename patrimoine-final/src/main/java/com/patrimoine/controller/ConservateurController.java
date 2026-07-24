package com.patrimoine.controller;

import com.patrimoine.service.SignalementService;
import com.patrimoine.service.SiteService;
import com.patrimoine.service.UserService;
import com.patrimoine.service.VisiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/conservateur")
public class ConservateurController {

    private final UserService userService;
    private final SiteService siteService;
    private final VisiteService visiteService;
    private final SignalementService signalementService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("nbSites", siteService.listerTous().size());
        model.addAttribute("nbVisites", visiteService.listerToutes().size());
        model.addAttribute("guidesEnAttente", userService.guidesEnAttente());
        model.addAttribute("signalements", signalementService.enAttente());
        return "dashboard/conservateur";
    }

    // ---- Validation des comptes guides ----
    @GetMapping("/guides")
    public String guidesEnAttente(Model model) {
        model.addAttribute("guides", userService.guidesEnAttente());
        return "guides/validation";
    }

    @PostMapping("/guides/{id}/valider")
    public String validerGuide(@PathVariable Long id) {
        userService.validerGuide(id);
        return "redirect:/conservateur/guides";
    }

    @PostMapping("/guides/{id}/refuser")
    public String refuserGuide(@PathVariable Long id) {
        userService.refuserGuide(id);
        return "redirect:/conservateur/guides";
    }

    // ---- Moderation des avis signales ----
    @GetMapping("/moderation")
    public String moderation(Model model) {
        model.addAttribute("signalements", signalementService.enAttente());
        return "avis/moderation";
    }

    @PostMapping("/moderation/{id}/resoudre")
    public String resoudreSignalement(@PathVariable Long id) {
        signalementService.resoudre(id);
        return "redirect:/conservateur/moderation";
    }
}
