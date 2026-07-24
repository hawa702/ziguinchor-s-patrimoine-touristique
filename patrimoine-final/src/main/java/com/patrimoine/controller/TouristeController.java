package com.patrimoine.controller;

import com.patrimoine.model.User;
import com.patrimoine.service.ReservationService;
import com.patrimoine.service.SiteService;
import com.patrimoine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/touriste")
public class TouristeController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final SiteService siteService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User touriste = userService.getUserConnecte(auth);
        model.addAttribute("touriste", touriste);
        model.addAttribute("reservations", reservationService.reservationsDuTouriste(touriste));
        model.addAttribute("sites", siteService.listerTous());
        return "dashboard/touriste";
    }
}
