package com.patrimoine.controller;

import com.patrimoine.model.Reservation;
import com.patrimoine.model.User;
import com.patrimoine.service.QrCodeService;
import com.patrimoine.service.ReservationService;
import com.patrimoine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final QrCodeService qrCodeService;

    @GetMapping
    public String mesReservations(Authentication auth, Model model) {
        User touriste = userService.getUserConnecte(auth);
        model.addAttribute("reservations", reservationService.reservationsDuTouriste(touriste));
        return "reservations/list";
    }

    @PostMapping("/visite/{visiteId}")
    public String reserver(@PathVariable Long visiteId, Authentication auth, Model model) {
        User touriste = userService.getUserConnecte(auth);
        try {
            Reservation reservation = reservationService.reserver(visiteId, touriste);
            return "redirect:/reservations/" + reservation.getId() + "/confirmation";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/visites/" + visiteId + "?erreur=" + e.getMessage();
        }
    }

    @GetMapping("/{id}/confirmation")
    public String confirmation(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.trouverParId(id);
        String qrCodeBase64 = qrCodeService.genererQrCodeBase64(reservation.getQrCodeHash());
        model.addAttribute("reservation", reservation);
        model.addAttribute("qrCode", qrCodeBase64);
        return "reservations/confirmation";
    }

    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id) {
        reservationService.annuler(id);
        return "redirect:/reservations";
    }
}
