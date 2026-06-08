# Documentation — Fichiers par Sprint
## Plateforme de Formation — PFE

---

# SPRINT 1 — Authentification JWT & Gestion des Formateurs

## Backend — Spring Boot

### Entités
| Fichier | Rôle |
|---------|------|
| `src/main/java/com/formation/entity/User.java` | Entité utilisateur (id, nom, prenom, email, password, role) — implémente UserDetails |
| `src/main/java/com/formation/entity/Role.java` | Enum : ADMIN, FORMATEUR, APPRENANT |

### Repositories
| Fichier | Rôle |
|---------|------|
| `src/main/java/com/formation/repository/UserRepository.java` | findByEmail, existsByEmail, findByRole |

### DTOs
| Fichier | Rôle |
|---------|------|
| `src/main/java/com/formation/dto/LoginRequest.java` | Corps de la requête login |
| `src/main/java/com/formation/dto/RegisterRequest.java` | Corps inscription apprenant |
| `src/main/java/com/formation/dto/AuthResponse.java` | Réponse avec token JWT |
| `src/main/java/com/formation/dto/UserResponse.java` | Données utilisateur en réponse |
| `src/main/java/com/formation/dto/CreateFormateurRequest.java` | Création formateur par admin |

### Services
| Fichier | Rôle |
|---------|------|
| `src/main/java/com/formation/service/UserService.java` | login, register, createFormateur, updateFormateur, deleteFormateur, getAllFormateurs |
| `src/main/java/com/formation/service/EmailService.java` | sendFormateurCredentials — envoi email avec mot de passe temporaire |

### Controllers
| Fichier | Rôle |
|---------|------|
| `src/main/java/com/formation/controller/AuthController.java` | POST /api/auth/login, POST /api/auth/register |
| `src/main/java/com/formation/controller/UserController.java` | GET/POST/PUT/DELETE /api/users/formateurs |

### Sécurité
| Fichier | Rôle |
|---------|------|
| `src/main/java/com/formation/security/JwtUtil.java` | Génération et validation des tokens JWT |
| `src/main/java/com/formation/security/JwtAuthenticationFilter.java` | Filtre HTTP — vérifie le Bearer token sur chaque requête |
| `src/main/java/com/formation/config/SecurityConfig.java` | Configuration Spring Security — routes publiques/protégées, CORS |
| `src/main/java/com/formation/config/PasswordEncoderConfig.java` | Bean BCryptPasswordEncoder |
| `src/main/java/com/formation/config/DataInitializer.java` | Création du premier compte ADMIN au démarrage |

### Configuration
| Fichier | Rôle |
|---------|------|
| `src/main/resources/application.properties` | DB PostgreSQL, JWT secret/expiration, SMTP Gmail, CORS |
| `pom.xml` | Dépendances : Spring Boot, Security, JWT, Mail, Lombok, PostgreSQL |

---

## Frontend — Angular 17

### Modèles
| Fichier | Rôle |
|---------|------|
| `src/app/models/user.model.ts` | LoginRequest, RegisterRequest, AuthResponse, UserResponse, CreateFormateurRequest |

### Services
| Fichier | Rôle |
|---------|------|
| `src/app/services/auth.service.ts` | login, register, logout, getCurrentUser, getToken, isAdmin, isApprenant, isFormateur |
| `src/app/services/user.service.ts` | getAllFormateurs, createFormateur, updateFormateur, deleteFormateur |

### Composants
| Dossier | Fichiers | Rôle |
|---------|---------|------|
| `components/login/` | `.ts` `.html` `.scss` | Page de connexion — formulaire email/password |
| `components/register/` | `.ts` `.html` `.scss` | Page d'inscription apprenant |

### Guards & Interceptors
| Fichier | Rôle |
|---------|------|
| `src/app/guards/auth.guard.ts` | authGuard (connecté), adminGuard (ADMIN uniquement) |
| `src/app/guards/role.guard.ts` | roleGuard(['ROLE']) — accès par rôle |
| `src/app/interceptors/jwt.interceptor.ts` | Ajoute Bearer token à toutes les requêtes HTTP |

### Configuration
| Fichier | Rôle |
|---------|------|
| `src/app/app.config.ts` | provideRouter, provideHttpClient, JwtInterceptor, provideAnimations |
| `src/app/app.routes.ts` | Routes : /login, /register |
| `src/app/utils/http-error.util.ts` | Fonction mapHttpErrorMessage — uniformise les erreurs HTTP |

---

# SPRINT 2 — Formations & Inscriptions

## Backend — Spring Boot

### Entités
| Fichier | Rôle |
|---------|------|
| `entity/Formation.java` | id, titre, description, duree, dateDebut, dateFin, capaciteMax, prix, statut, formateurs[] |
| `entity/FormationStatut.java` | Enum : PLANIFIEE, EN_COURS, TERMINEE |
| `entity/Inscription.java` | id, formation, apprenant, statut, dateInscription, motifRejet |
| `entity/InscriptionStatut.java` | Enum : EN_ATTENTE, ACCEPTEE, REJETEE, ANNULEE |

### Repositories
| Fichier | Rôle |
|---------|------|
| `repository/FormationRepository.java` | findByStatut, findByFormateurId, searchByKeyword, findCatalogue |
| `repository/InscriptionRepository.java` | findByFormationIdAndApprenantId, findByStatut, findByFormationId, findByApprenantId |

### DTOs
| Fichier | Rôle |
|---------|------|
| `dto/FormationCreateDTO.java` | Création de formation |
| `dto/FormationUpdateDTO.java` | Modification de formation |
| `dto/FormationResponseDTO.java` | Réponse avec détails formation |
| `dto/InscriptionDTO.java` | Demande d'inscription |
| `dto/InscriptionResponseDTO.java` | Réponse inscription |
| `dto/InscriptionAcceptanceDTO.java` | Acceptation inscription |
| `dto/InscriptionRejectionDTO.java` | Rejet avec motif |

### Services
| Fichier | Rôle |
|---------|------|
| `service/FormationService.java` | CRUD formations, catalogue, recherche, statistiques places |
| `service/InscriptionService.java` | inscrire, accepter, rejeter, annuler, lister par statut |
| `service/EmailService.java` | +sendInscriptionConfirmationEmail, +sendInscriptionAcceptanceEmail, +sendInscriptionRejectionEmail, +sendFormationModificationEmail, +sendAdminNewInscriptionNotification |

### Controllers
| Fichier | Rôle |
|---------|------|
| `controller/FormationController.java` | CRUD /api/formations + catalogue public |
| `controller/InscriptionController.java` | /api/inscriptions — s'inscrire, accepter, rejeter, annuler |

### Exceptions
| Fichier | Code HTTP |
|---------|-----------|
| `exception/FormationNotFoundException.java` | 404 |
| `exception/InscriptionNotFoundException.java` | 404 |
| `exception/InscriptionAlreadyExistsException.java` | 409 |
| `exception/NoAvailablePlacesException.java` | 409 |
| `exception/InvalidFormationDatesException.java` | 409 |
| `exception/InvalidFormationDataException.java` | 409 |
| `exception/CannotDeleteFormationException.java` | 409 |
| `exception/GlobalExceptionHandler.java` | @ControllerAdvice — centralise les erreurs |

---

## Frontend — Angular 17

### Modèles
| Fichier | Rôle |
|---------|------|
| `models/formation.model.ts` | Formation, FormationRequest, Inscription, StatutFormation, StatutInscription, **PageResponse\<T\>** |

### Services
| Fichier | Rôle |
|---------|------|
| `services/formation.service.ts` | getAllFormations, getCatalogue, createFormation, updateFormation, deleteFormation, searchFormations |
| `services/inscription.service.ts` | sInscrire, accepter, rejeter, annuler, getInscriptionsEnAttente, getMesInscriptions |

### Composants
| Dossier | Fichiers | Rôle |
|---------|---------|------|
| `components/admin-dashboard/` | `.ts` `.html` `.scss` | Dashboard admin — stats formateurs/formations + actions rapides |
| `components/admin-formations/` | `.ts` `.html` `.scss` | Liste formations admin — tableau + pagination + CRUD |
| `components/formation-create-edit/` | `.ts` `.html` `.scss` | Formulaire création/modification formation |
| `components/admin-inscriptions/` | `.ts` `.html` `.scss` | Liste inscriptions EN_ATTENTE — accepter/rejeter |
| `components/admin-formateurs/` | `.ts` `.html` `.scss` | Gestion formateurs — tableau + modale ajout/modif |
| `components/catalogue-formations/` | `.ts` `.html` `.scss` | Catalogue public — filtres statut/prix/dates + s'inscrire |
| `components/formation-detail/` | `.ts` `.html` `.scss` | Détail formation publique |
| `components/inscrits-formation/` | `.ts` `.html` `.scss` | Liste inscrits d'une formation (admin/formateur) |
| `components/mes-inscriptions/` | `.ts` `.html` `.scss` | Inscriptions de l'apprenant connecté |

### Routes ajoutées
```
/catalogue, /formations/:id, /mes-inscriptions
/admin/dashboard, /admin/formations, /admin/formations/create
/admin/formations/edit/:id, /admin/inscriptions, /admin/formateurs
/admin/formations/:id/inscrits
```

---

# SPRINT 3 — Séances, Présences, Notes, Planning, Salles

## Backend — Spring Boot

### Entités
| Fichier | Rôle |
|---------|------|
| `entity/Seance.java` | id, titre, formation, salle, formateur, dateHeureDebut, dateHeureFin, statut |
| `entity/SeanceStatut.java` | Enum : PLANIFIEE, EN_COURS, TERMINEE, ANNULEE |
| `entity/Salle.java` | id, nom, capacite, localisation, equipements, disponible |
| `entity/Presence.java` | id, seance, apprenant, statut, commentaire, enregistrePar |
| `entity/PresenceStatut.java` | Enum : PRESENT, ABSENT, RETARD, EXCUSE |
| `entity/Note.java` | id, seance, apprenant, valeur, coefficient, typeEvaluation, enregistrePar |
| `entity/TypeEvaluation.java` | Enum : EXAMEN, DEVOIR, QUIZ, PROJET, CONTROLE |

### Repositories
| Fichier | Rôle |
|---------|------|
| `repository/SeanceRepository.java` | findByFormationId, findByFormateurId, findPlanningFormateur, findConflits |
| `repository/SalleRepository.java` | findByDisponible, findByCapaciteGreaterThanEqual |
| `repository/PresenceRepository.java` | findBySeanceId, countPresencesEffectives, countSeancesTerminees |
| `repository/NoteRepository.java` | findByApprenantIdAndSeanceFormationId, calculerMoyennePonderee |

### DTOs (16 fichiers)
| Fichier | Rôle |
|---------|------|
| `dto/SeanceCreateDTO.java` / `SeanceUpdateDTO.java` / `SeanceResponseDTO.java` | CRUD séances |
| `dto/SalleCreateDTO.java` / `SalleResponseDTO.java` | CRUD salles |
| `dto/PresenceCreateDTO.java` / `PresenceBulkDTO.java` / `PresenceItemDTO.java` / `PresenceResponseDTO.java` | Enregistrement présences |
| `dto/NoteCreateDTO.java` / `NoteBulkDTO.java` / `NoteItemDTO.java` / `NoteResponseDTO.java` | Enregistrement notes |
| `dto/StatistiquesApprenantDTO.java` | Stats présence + notes par apprenant |
| `dto/PlanningDTO.java` | Planning hebdomadaire |
| `dto/ConflitDTO.java` | Conflits horaires |

### Services
| Fichier | Rôle |
|---------|------|
| `service/SeanceService.java` | CRUD séances, vérification conflits horaires, planning |
| `service/SalleService.java` | CRUD salles, disponibilité |
| `service/PresenceService.java` | Enregistrement présences, bulk, taux présence |
| `service/NoteService.java` | Enregistrement notes, bulk, calcul moyenne pondérée |
| `service/SuiviPedagogiqueService.java` | Statistiques apprenant, progression, suivi formation |
| `service/EmailService.java` | +sendSeanceAssignmentEmail, +sendSeanceModificationEmail, +sendSeanceAnnulationEmail |

### Controllers
| Fichier | Endpoints |
|---------|-----------|
| `controller/SeanceController.java` | /api/seances — CRUD + conflits + planning |
| `controller/SalleController.java` | /api/salles — CRUD + disponibilité |
| `controller/PresenceController.java` | /api/presences — enregistrer, bulk, taux |
| `controller/NoteController.java` | /api/notes — enregistrer, bulk, moyenne |
| `controller/SuiviPedagogiqueController.java` | /api/suivi — stats apprenant, stats formation |
| `controller/PlanningController.java` | /api/planning — admin, formateur, apprenant |

### Exceptions (ajoutées au GlobalExceptionHandler)
| Fichier | Code HTTP |
|---------|-----------|
| `exception/SeanceNotFoundException.java` | 404 |
| `exception/SalleNotFoundException.java` | 404 |
| `exception/PresenceNotFoundException.java` | 404 |
| `exception/NoteNotFoundException.java` | 404 |
| `exception/ConflitHoraireException.java` | 409 |
| `exception/CannotDeleteSalleException.java` | 409 |
| `exception/CannotDeleteSeanceException.java` | 409 |
| `exception/FormateurNonResponsableException.java` | 403 |
| `exception/ApprenantNonInscritException.java` | 400 |
| `exception/NoteInvalideException.java` | 400 |
| `exception/InvalidSeanceDatesException.java` | 400 |
| `exception/UnauthorizedActionException.java` | 403 |
| `exception/GlobalExceptionHandler.java` | Mis à jour avec tous les nouveaux handlers |

---

## Frontend — Angular 17

### Modèles
| Fichier | Rôle |
|---------|------|
| `models/planning.model.ts` | Seance, Presence, Note, StatistiquesApprenant, PlanningDTO, ConflitDTO, SeanceStatut, PresenceStatut, TypeEvaluation |

### Services
| Fichier | Rôle |
|---------|------|
| `services/seance.service.ts` | CRUD séances, planning, conflits |
| `services/salle.service.ts` | CRUD salles, disponibilité |
| `services/presence.service.ts` | enregistrer, bulk, taux présence |
| `services/note.service.ts` | enregistrer, bulk, moyenne |
| `services/suivi.service.ts` | getStatistiquesApprenant, getStatistiquesFormation |

### Composants
| Dossier | Fichiers | Rôle |
|---------|---------|------|
| `components/admin-seances/` | `.ts` `.html` `.scss` | Tableau séances admin — CRUD + filtres + planning |
| `components/admin-salles/` | `.ts` `.html` `.scss` | Tableau salles — CRUD + vérif disponibilité |
| `components/saisie-presences/` | `.ts` `.html` `.scss` | Formateur — saisie présences par séance |
| `components/saisie-notes/` | `.ts` `.html` `.scss` | Formateur — saisie notes par séance |
| `components/planning-calendrier/` | `.ts` `.html` `.scss` | Calendrier hebdomadaire (admin/formateur/apprenant) |
| `components/mes-presences-apprenant/` | `.ts` `.html` `.scss` | Apprenant — liste de ses présences |
| `components/mes-notes-apprenant/` | `.ts` `.html` `.scss` | Apprenant — liste de ses notes |
| `components/suivi-apprenant/` | `.ts` `.html` `.scss` | Apprenant — tableau suivi pédagogique complet |
| `components/suivi-formation/` | `.ts` `.html` `.scss` | Admin/Formateur — suivi de tous les apprenants d'une formation |

### Routes ajoutées
```
/admin/salles, /admin/seances, /admin/formations/:id/suivi
/mon-planning, /mes-notes, /mes-presences, /mon-suivi
/formateur/planning, /formateur/seances/:id/presences, /formateur/seances/:id/notes
/formateur/formations/:id/suivi
```

---

# SPRINT 4 — Gestion Financière & Statistiques

## Backend — Spring Boot

### Entités
| Fichier | Rôle |
|---------|------|
| `entity/Tarif.java` | id, formation (FK), prixUnitaire, prixReduit, seuilGroupe, devise, actif + méthode getPrixApplicable() |
| `entity/Facture.java` | id, numeroFacture, apprenant, formation, dateEmission, dateEcheance, montantHT/TVA/Total, statut, paiements[] |
| `entity/Paiement.java` | id, facture, montant, datePaiement, modePaiement, statut, enregistrePar, validePar |
| `entity/Rapport.java` | id, titre, type, dateDebut, dateFin, contenuJSON, format, generePar |
| `entity/FactureStatut.java` | Enum : EN_ATTENTE, PARTIELLEMENT_PAYEE, PAYEE, ANNULEE |
| `entity/PaiementStatut.java` | Enum : EN_ATTENTE, VALIDE, REJETE |
| `entity/ModePaiement.java` | Enum : VIREMENT, CARTE, CHEQUE, ESPECES, AUTRE |
| `entity/TypeRapport.java` | Enum : INSCRIPTIONS, PRESENCES, RESULTATS, FINANCES, GENERAL |
| `entity/RapportFormat.java` | Enum : PDF, EXCEL, JSON, HTML |

### Repositories
| Fichier | Rôle |
|---------|------|
| `repository/TarifRepository.java` | findByFormationId, findByActif, existsByFormationId |
| `repository/FactureRepository.java` | findByApprenantId, findByStatut, findFacturesExpirees, calculateRevenu, calculateMontantImpaye, findNumerosByPrefix |
| `repository/PaiementRepository.java` | findByFactureId, findByApprenantId, calculateMontantPayeFacture, countPaiementsEnAttente |
| `repository/RapportRepository.java` | findByType, findByDateGenerationBetween, findByGenereParId |

### DTOs (13 fichiers)
| Fichier | Rôle |
|---------|------|
| `dto/TarifCreateDTO.java` / `TarifResponseDTO.java` | CRUD tarifs |
| `dto/FactureCreateDTO.java` / `FactureResponseDTO.java` / `FactureUpdateStatutDTO.java` | CRUD factures |
| `dto/PaiementCreateDTO.java` / `PaiementResponseDTO.java` / `PaiementValidationDTO.java` | CRUD paiements |
| `dto/RapportRequestDTO.java` / `RapportResponseDTO.java` | Génération rapports |
| `dto/StatistiquesGlobalesDTO.java` | KPIs globaux de la plateforme |
| `dto/StatistiquesFormationDTO.java` | Stats financières par formation |
| `dto/TableauDeBordDTO.java` | Vue synthétique admin — KPIs + top formations/apprenants |

### Services
| Fichier | Rôle |
|---------|------|
| `service/TarifService.java` | CRUD tarifs, validation, prix applicable selon seuil groupe |
| `service/FactureService.java` | Création facture (calcul HT/TVA/TTC), cycle de vie statut, annulation, téléchargement PDF |
| `service/PaiementService.java` | Enregistrer paiement, valider/rejeter (admin), mise à jour statut facture automatique |
| `service/RapportService.java` | Génération rapports JSON (inscriptions, présences, résultats, finances, général) |
| `service/StatistiquesService.java` | Statistiques globales, par formation, tableau de bord |
| `service/PdfService.java` | Génération PDF facture avec OpenPDF |
| `service/EmailService.java` | +sendFactureEmail, +sendPaiementConfirmationEmail, +sendPaiementValidationEmail, +sendRappelPaiementEmail, +sendRapportEmail |

### Controllers
| Fichier | Endpoints |
|---------|-----------|
| `controller/TarifController.java` | /api/tarifs — CRUD + prix applicable |
| `controller/FactureController.java` | /api/factures — CRUD + PDF + mes-factures |
| `controller/PaiementController.java` | /api/paiements — enregistrer + valider + rejeter |
| `controller/RapportController.java` | /api/rapports — générer + télécharger |
| `controller/StatistiquesController.java` | /api/statistiques — globales + formation + tableau-de-bord |

### Exceptions (ajoutées au GlobalExceptionHandler)
| Fichier | Code HTTP |
|---------|-----------|
| `exception/TarifNotFoundException.java` | 404 |
| `exception/TarifMutableException.java` | 409 |
| `exception/FactureNotFoundException.java` | 404 |
| `exception/FactureNonAnnulableException.java` | 409 |
| `exception/PaiementNotFoundException.java` | 404 |
| `exception/MontantInvalidException.java` | 400 |
| `exception/FactureExpireException.java` | 409 |
| `exception/RapportGenerationException.java` | 500 |
| `exception/GlobalExceptionHandler.java` | Mis à jour — 8 nouveaux handlers + IllegalStateException |

### Utilitaires & Configuration
| Fichier | Rôle |
|---------|------|
| `util/FactureNumberGenerator.java` | Génère numéro unique FACT-YYYY-NNNN (synchronisé) |
| `resources/application.properties` | +app.tva.rate=0.20, +config Jackson dates |
| `resources/db/sprint4_schema.sql` | Tables tarifs, factures, paiements, rapports + index + données test |
| `pom.xml` | +openpdf 1.3.30 (génération PDF) |

---

## Frontend — Angular 17

### Modèles
| Fichier | Rôle |
|---------|------|
| `models/financial.model.ts` | Tarif, TarifRequest, Facture, FactureRequest, Paiement, PaiementRequest, Rapport, RapportRequest, StatistiquesGlobales, StatistiquesFormation, TableauDeBord, FormationStat, ApprenantStat + tous les types/enums |

### Services
| Fichier | Rôle |
|---------|------|
| `services/tarif.service.ts` | getAllTarifs, createTarif, updateTarif, deleteTarif, getTarifParFormation, getPrixApplicable |
| `services/facture.service.ts` | createFacture, getFacture, getAllFactures, getFacturesApprenant, updateStatut, annulerFacture, downloadFacturePDF (Blob), getMesFactures |
| `services/paiement.service.ts` | enregistrerPaiement, validerPaiement, rejeterPaiement, getPaiementsEnAttente, getPaiementsApprenant, getMesPaiements |
| `services/rapport.service.ts` | genererRapport, getRapport, getAllRapports, telechargerRapport (Blob), genererRapportInscriptions, genererRapportFinances, genererRapportPresences |
| `services/statistiques.service.ts` | getStatistiquesGlobales, getStatistiquesFormation, getTableauDeBord |

### Layouts partagés
| Dossier | Fichiers | Rôle |
|---------|---------|------|
| `layouts/admin-layout/` | `.ts` `.html` `.scss` | Sidebar fixe 250px avec 13 liens (Sprint 1-4) + router-outlet + override CSS des sidebars inline |
| `layouts/apprenant-layout/` | `.ts` `.html` `.scss` | Navbar fixe 64px avec logo + liens + avatar initiales + déconnexion + router-outlet |

### Composants Admin
| Dossier | Fichiers | Rôle |
|---------|---------|------|
| `components/admin-tarifs/` | `.ts` `.html` `.scss` | Tableau tarifs — CRUD + modale formation/prix/devise |
| `components/admin-factures/` | `.ts` `.html` `.scss` | Tableau factures — création (calcul TVA live) + panel détail + PDF + annulation |
| `components/admin-paiements/` | `.ts` `.html` `.scss` | Tableau paiements — onglets EN_ATTENTE/VALIDES/REJETES + modales valider/rejeter |
| `components/admin-rapports/` | `.ts` `.html` `.scss` | Génération rapports (5 types) + date picker + tableau historique + visionneuse JSON |
| `components/tableau-de-bord/` | `.ts` `.html` `.scss` | 8 KPIs + alertes factures/paiements + top 5 formations et apprenants |
| `components/statistiques-globales/` | `.ts` `.html` `.scss` | 8 cartes stats + date picker (Jour/Mois/Année) limité 2025-2026 + onglets |

### Composants Apprenant
| Dossier | Fichiers | Rôle |
|---------|---------|------|
| `components/mes-factures-apprenant/` | `.ts` `.html` `.scss` | Cards factures — statut badge + barre progression % + PDF + Payer maintenant |
| `components/mes-paiements-apprenant/` | `.ts` `.html` `.scss` | Tableau paiements — onglets Tous/En attente/Validés + badges mode |
| `components/enregistrer-paiement/` | `.ts` `.html` `.scss` | Formulaire paiement — résumé facture (lecture seule) + montant/date/mode + validation max |

### Routes ajoutées (app.routes.ts)
```
/admin (component: AdminLayoutComponent)
  ├── tarifs       → AdminTarifsComponent
  ├── factures     → AdminFacturesComponent
  ├── factures/:id/payer → EnregistrerPaiementComponent
  ├── paiements    → AdminPaiementsComponent
  ├── rapports     → AdminRapportsComponent
  ├── statistiques → StatistiquesGlobalesComponent
  └── tableau-de-bord → TableauDeBordComponent

'' (component: ApprenantLayoutComponent, canActivate: APPRENANT)
  ├── mes-inscriptions    → MesInscriptionsComponent
  ├── mon-planning        → PlanningCalendrierComponent
  ├── mes-notes           → MesNotesApprenantComponent
  ├── mes-presences       → MesPresencesApprenantComponent
  ├── mon-suivi           → SuiviApprenantComponent
  ├── mes-factures        → MesFacturesApprenantComponent
  ├── mes-paiements       → MesPaiementsApprenantComponent
  └── paiements/enregistrer/:factureId → EnregistrerPaiementComponent
```

---

## Récapitulatif Global

| Sprint | Backend (fichiers) | Frontend (fichiers) | Total |
|--------|--------------------|---------------------|-------|
| Sprint 1 | 15 | 12 | 27 |
| Sprint 2 | 22 | 14 | 36 |
| Sprint 3 | 30 | 14 | 44 |
| Sprint 4 | 46 | 22 | 68 |
| **TOTAL** | **113** | **62** | **175** |

---

## Stack Technique

| Couche | Technologie | Version |
|--------|-------------|---------|
| Backend | Spring Boot | 3.2.5 |
| Langage backend | Java | 17 |
| Base de données | PostgreSQL | 15+ |
| Sécurité | Spring Security + JWT (jjwt) | 0.12.5 |
| Email | Spring Mail + Gmail SMTP | — |
| PDF | OpenPDF | 1.3.30 |
| Frontend | Angular | 17.3.0 |
| Langage frontend | TypeScript | 5.x |
| Styles | SCSS | — |
| HTTP | Angular HttpClient + JWT Interceptor | — |
| Routing | Angular Router (Standalone + Lazy Loading) | — |
