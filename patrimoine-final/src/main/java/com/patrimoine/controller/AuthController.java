package com.patrimoine.controller;

import com.patrimoine.model.User;
import com.patrimoine.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/inscription/touriste")
    public String formulaireTouriste(Model model) {
        model.addAttribute("user", new User());
        return "inscription-touriste";
    }

    @PostMapping("/inscription/touriste")
    public String inscrireTouriste(@Valid @ModelAttribute("user") User user,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "inscription-touriste";
        }
        if (userService.emailExiste(user.getEmail())) {
            model.addAttribute("erreur", "Cet email est déjà utilisé.");
            return "inscription-touriste";
        }
        userService.inscrireTouriste(user);
        model.addAttribute("succes", "Compte créé avec succès. Vous pouvez vous connecter.");
        return "login";
    }

    @GetMapping("/inscription/guide")
    public String formulaireGuide(Model model) {
        model.addAttribute("user", new User());
        return "inscription-guide";
    }

    @PostMapping("/inscription/guide")
    public String inscrireGuide(@Valid @ModelAttribute("user") User user,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "inscription-guide";
        }
        if (userService.emailExiste(user.getEmail())) {
            model.addAttribute("erreur", "Cet email est déjà utilisé.");
            return "inscription-guide";
        }
        userService.inscrireGuide(user);
        model.addAttribute("succes", "Compte créé. Il doit être validé par un conservateur avant votre première connexion.");
        return "login";
    }
}
