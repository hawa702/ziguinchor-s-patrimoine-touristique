package com.patrimoine.controller;

import com.patrimoine.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SiteService siteService;

    @GetMapping({"/", "/accueil"})
    public String accueil(Model model) {
        model.addAttribute("sites", siteService.listerTous());
        return "accueil";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse";
    }
}
