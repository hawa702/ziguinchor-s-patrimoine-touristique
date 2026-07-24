package com.patrimoine.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /** Sauvegarde le fichier dans le dossier ./uploads (racine du projet) et renvoie l'URL relative.
     *  Ce dossier est servi dynamiquement via WebConfig (voir /uploads/**), donc pas besoin de
     *  redémarrer l'application après chaque upload. */
    public String sauvegarder(MultipartFile file) throws IOException {
        Path dossier = Paths.get(uploadDir);
        if (!Files.exists(dossier)) {
            Files.createDirectories(dossier);
        }
        String extension = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf("."));
        }
        String nomFichier = UUID.randomUUID() + extension;
        Path destination = dossier.resolve(nomFichier);
        Files.copy(file.getInputStream(), destination);
        return "/" + uploadDir + "/" + nomFichier;
    }
}
