# Gestion de Patrimoine Touristique

Application web sécurisée développée avec **Spring Boot**, **Thymeleaf** et **Spring Security**,
permettant la gestion de sites touristiques (monuments, parcs, musées), la planification de visites,
la réservation de places avec génération de QR Code, et la gestion des avis/notations.

## Stack technique

- Java 17
- Spring Boot 3.3 (Web, Security, Data JPA, Validation, Thymeleaf)
- Base de données H2 (fichier local, aucune installation requise)
- ZXing (génération de QR Code)
- Lombok
- Maven

## Ouvrir le projet dans IntelliJ IDEA

1. **File → Open...** puis sélectionner le dossier `patrimoine-touristique` (celui contenant `pom.xml`).
2. IntelliJ détecte automatiquement le projet Maven et télécharge les dépendances (barre de progression en bas à droite). Patientez la fin du téléchargement.
3. **Activer Lombok** (nécessaire car le code utilise `@Data`, `@RequiredArgsConstructor`, etc.) :
   - `File → Settings → Plugins` → vérifier que le plugin **Lombok** est installé (il l'est par défaut sur les versions récentes) et activé.
   - `File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors` → cocher **Enable annotation processing**.
4. Vérifier que le **SDK du projet** est en Java 17 : `File → Project Structure → Project → SDK`.
5. Lancer l'application :
   - Ouvrir `src/main/java/com/patrimoine/PatrimoineApplication.java`
   - Clic droit → **Run 'PatrimoineApplication'** (ou cliquer sur la flèche verte à côté de `main`)

L'application démarre sur **http://localhost:8080**

## Comptes de démonstration

Au premier démarrage, 3 comptes sont automatiquement créés (mot de passe : `password123`) :

| Rôle | Email |
|---|---|
| Conservateur | conservateur@patrimoine.sn |
| Guide (déjà validé) | guide@patrimoine.sn |
| Touriste | touriste@patrimoine.sn |

## Parcours de test conseillé

1. Se connecter en tant que **conservateur** → créer un site (avec coordonnées GPS et horaires) → ajouter des photos → planifier une visite en assignant le guide de démo.
2. Se connecter en tant que **touriste** → parcourir les sites → réserver une place sur la visite créée → voir le ticket avec QR Code → confirmer ou annuler la réservation → déposer un avis (note + commentaire) sur le site.
3. Se connecter en tant que **guide** → consulter l'agenda du jour → répondre à l'avis reçu.
4. Créer un **nouveau compte guide** via "Inscription" → se reconnecter en conservateur → le valider dans "Validation guides" avant qu'il puisse se connecter.
5. Tester le **signalement** d'un avis (bouton "Signaler") puis, en conservateur, le supprimer depuis "Modération".

## Fonctionnalités implémentées

- **CRUD complet des sites** (création, lecture, modification, suppression) réservé au conservateur
- **Géolocalisation** : latitude/longitude par site + recherche par proximité GPS (formule de Haversine)
- **Upload de médias** (photos/vidéos) rattachés à un site, servis dynamiquement depuis le dossier `uploads/`
- **Recherche avancée** par ville et par type de site
- **Planification de visites** (date, heure, guide assigné, capacité)
- **Réservation de places** avec vérification du stock disponible (logique métier)
- **Confirmation / annulation** de réservation
- **Génération de QR Code** pour chaque ticket de réservation (encodage du code unique du ticket)
- **Avis** sur un site ou sur un guide, notation 5 étoiles, **calcul automatique de la note moyenne**
- **Signalement d'avis inapproprié** et modération (suppression par le conservateur)
- **Validation des comptes guides** par le conservateur (un guide non validé ne peut pas se connecter)
- **Spring Security** : formulaire de connexion/déconnexion, routes protégées par rôle (`ROLE_CONSERVATEUR`, `ROLE_GUIDE`, `ROLE_TOURISTE`), mots de passe chiffrés en BCrypt

## Structure du projet

```
src/main/java/com/patrimoine/
 ├── config/        → Sécurité, données de démo, config des fichiers uploadés
 ├── model/          → Entités JPA (Utilisateur, Site, Media, Visite, Reservation, Avis)
 ├── repository/     → Interfaces Spring Data JPA
 ├── service/        → Logique métier (CRUD, stock, QR Code, notes moyennes...)
 └── controller/     → Contrôleurs MVC (Thymeleaf)

src/main/resources/
 ├── templates/      → Vues Thymeleaf
 ├── static/css/     → Feuille de style
 └── application.properties
```

## Pistes d'amélioration (pour aller plus loin dans le cadre du cours)

- Ajouter des tests unitaires/intégration (JUnit + `spring-security-test`)
- Ajouter la pagination sur les listes de sites/visites
- Passer la base H2 à MySQL/PostgreSQL en production
- Ajouter un système de notifications (email) lors de la validation d'un guide ou de la confirmation d'une réservation
- Ajouter une carte interactive (Leaflet/Google Maps) pour visualiser les sites géolocalisés

---

## Mise à jour majeure — alignement sur le modèle de données du cours

Le schéma de base de données a été entièrement aligné sur le modèle fourni par le professeur :
**User, GuideDetails, Site, Media, Visite, Reservation, Avis, Signalement** (8 entités).

### ⚠️ Important avant de relancer ce projet

Si tu avais déjà lancé une version précédente, **supprime l'ancien dossier `data/`** à la racine
du projet (ou le dossier `data/` du projet précédent) avant de démarrer celui-ci. L'ancien schéma
(table `utilisateurs`, colonnes différentes) est incompatible avec le nouveau (table `users`,
`guide_details`, `signalements`...). Sans ça, l'application risque de planter au démarrage ou
d'afficher des données incohérentes.

### Changements principaux
- `Utilisateur` → `User` (id, nom, prenom, email, passwordHash, telephone, role [TOURIST, GUIDE, ADMIN])
- Nouvelle entité `GuideDetails` (numéro de licence, biographie, note moyenne, statut de vérification) — liée en 1-1 à `User`
- `Site` : horaires fusionnés en un seul champ texte + ajout de `capaciteAccueil`
- `Media` : champ renommé `url`, type `IMAGE`/`VIDEO`
- `Visite` : date + heure fusionnées en `dateHeureDebut`, ajout de `duree` (minutes), `placesDisponibles` stocké en base (plus calculé à la volée)
- `Reservation` : chaque réservation = 1 place, statuts simplifiés à `CONFIRME`/`ANNULE`, champ `qrCodeHash`
- `Avis` : lié à une `Reservation` (et non plus directement à un site/guide) — la note profite à la fois au site et au guide de la visite concernée
- Nouvelle entité `Signalement` (avis_id, motif, statut `EN_ATTENTE`/`RESOLU`) — la modération est maintenant tracée avec un vrai statut plutôt qu'un simple booléen

### Ce qui n'a pas changé
Tous les templates visuels (page d'accueil Ziguinchor, styles, images, galerie) sont restés identiques.
Seuls les noms de champs dans les formulaires ont été adaptés au nouveau modèle.
