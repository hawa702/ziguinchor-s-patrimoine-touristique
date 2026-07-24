package com.patrimoine.service;

import com.patrimoine.model.Avis;
import com.patrimoine.model.Signalement;
import com.patrimoine.model.StatutSignalement;
import com.patrimoine.repository.SignalementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignalementService {

    private final SignalementRepository signalementRepository;

    public void signaler(Avis avis, String motif) {
        Signalement s = new Signalement();
        s.setAvis(avis);
        s.setMotif(motif);
        s.setStatut(StatutSignalement.EN_ATTENTE);
        signalementRepository.save(s);
    }

    public List<Signalement> enAttente() {
        return signalementRepository.findByStatut(StatutSignalement.EN_ATTENTE);
    }

    public void resoudre(Long signalementId) {
        Signalement s = signalementRepository.findById(signalementId)
                .orElseThrow(() -> new IllegalArgumentException("Signalement introuvable"));
        s.setStatut(StatutSignalement.RESOLU);
        signalementRepository.save(s);
    }
}
