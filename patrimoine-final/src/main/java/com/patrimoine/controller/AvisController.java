package com.patrimoine.controller;

import com.patrimoine.model.Reservation;
import com.patrimoine.service.AvisService;
import com.patrimoine.service.ReservationService;
import com.patrimoine.service.SignalementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/avis")
public class AvisController {

    private final AvisService avisService;
    private final ReservationService reservationService;
    private final SignalementService signalementService;

    // ---- Depot d'un avis a partir d'une reservation confirmee ----
    @PostMapping("/nouveau/reservation/{reservationId}")
    public String deposerAvis(@PathVariable Long reservationId,
                               @RequestParam int note,
                               @RequestParam String commentaire) {
        Reservation reservation = reservationService.trouverParId(reservationId);
        avisService.deposerAvis(reservation, note, commentaire);
        return "redirect:/reservations";
    }

    // ---- Reponse du guide a un avis ----
    @PostMapping("/{avisId}/repondre")
    public String repondre(@PathVariable Long avisId, @RequestParam String reponse) {
        avisService.repondre(avisId, reponse);
        return "redirect:/guide/dashboard";
    }

    // ---- Signalement d'un avis inapproprie (modération) ----
    @PostMapping("/{avisId}/signaler")
    public String signaler(@PathVariable Long avisId, @RequestParam String motif) {
        signalementService.signaler(avisService.trouverParId(avisId), motif);
        return "redirect:/conservateur/dashboard";
    }

    // ---- Moderation : suppression d'un avis signale (admin) ----
    @PostMapping("/{avisId}/supprimer")
    public String supprimer(@PathVariable Long avisId) {
        avisService.supprimer(avisId);
        return "redirect:/conservateur/moderation";
    }
}
