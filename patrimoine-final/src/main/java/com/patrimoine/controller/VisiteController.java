package com.patrimoine.controller;

import com.patrimoine.model.RoleType;
import com.patrimoine.model.Visite;
import com.patrimoine.service.SiteService;
import com.patrimoine.service.UserService;
import com.patrimoine.service.VisiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.patrimoine.model.Site;
import com.patrimoine.model.User;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@Controller
@RequiredArgsConstructor
@RequestMapping("/visites")
public class VisiteController {

    private final VisiteService visiteService;
    private final SiteService siteService;
    private final UserService userService;

    /** Permet de lier les <select> du formulaire (qui envoient un ID) directement aux entités Site/User */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Site.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null || text.isBlank() ? null : siteService.trouverParId(Long.valueOf(text)));
            }
        });
        binder.registerCustomEditor(User.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null || text.isBlank() ? null : userService.trouverParId(Long.valueOf(text)));
            }
        });
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("visites", visiteService.listerToutes());
        return "visites/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("visite", visiteService.trouverParId(id));
        return "visites/detail";
    }

    @GetMapping("/nouvelle")
    public String formulaireCreation(Model model) {
        model.addAttribute("visite", new Visite());
        model.addAttribute("sites", siteService.listerTous());
        model.addAttribute("guides", userService.guidesValides());
        return "visites/form";
    }

    @PostMapping("/nouvelle")
    public String planifier(@Valid @ModelAttribute("visite") Visite visite, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sites", siteService.listerTous());
            model.addAttribute("guides", userService.guidesValides());
            return "visites/form";
        }
        Visite creee = visiteService.planifier(visite);
        return "redirect:/visites/" + creee.getId();
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("visite", visiteService.trouverParId(id));
        model.addAttribute("sites", siteService.listerTous());
        model.addAttribute("guides", userService.guidesValides());
        return "visites/form";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, @Valid @ModelAttribute("visite") Visite visite,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sites", siteService.listerTous());
            model.addAttribute("guides", userService.guidesValides());
            return "visites/form";
        }
        visiteService.modifier(id, visite);
        return "redirect:/visites/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        visiteService.supprimer(id);
        return "redirect:/visites";
    }
}
