package com.formation.config;

import com.formation.entity.*;
import com.formation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FormationRepository formationRepository;
    private final SalleRepository salleRepository;
    private final SeanceRepository seanceRepository;
    private final InscriptionRepository inscriptionRepository;
    private final PaiementRepository paiementRepository;

    @Override
    public void run(String... args) {
        log.info("🚀 Initialisation des données par défaut...");
        createUsers();
        createSalles();
        createFormationEtSeances();
        createInscriptions();
        fixFormationPrices();
        createPaiements();
        log.info("✅ Initialisation des données terminée!");
    }

    private void createUsers() {
        if (!userRepository.existsByEmail("admin@formation.com")) {
            userRepository.save(User.builder()
                    .nom("Admin").prenom("Super").email("admin@formation.com")
                    .password(passwordEncoder.encode("Admin@123")).role(Role.ADMIN).build());
            log.info("✅ Admin créé : admin@formation.com / Admin@123");
        }

        if (!userRepository.existsByEmail("apprenant1@test.com")) {
            userRepository.save(User.builder()
                    .nom("Dupont").prenom("Jean").email("apprenant1@test.com")
                    .password(passwordEncoder.encode("Apprenant@123")).role(Role.APPRENANT).build());
            log.info("✅ Apprenant créé : apprenant1@test.com / Apprenant@123");
        }

        if (!userRepository.existsByEmail("sophie.martin@email.com")) {
            userRepository.save(User.builder()
                    .nom("Martin").prenom("Sophie").email("sophie.martin@email.com")
                    .password(passwordEncoder.encode("Sophie@123")).role(Role.APPRENANT).build());
            log.info("✅ Apprenant créé : sophie.martin@email.com / Sophie@123");
        }

        if (!userRepository.existsByEmail("formateur1@test.com")) {
            userRepository.save(User.builder()
                    .nom("Martin").prenom("Pierre").email("formateur1@test.com")
                    .password(passwordEncoder.encode("Formateur@123")).role(Role.FORMATEUR).build());
            log.info("✅ Formateur créé : formateur1@test.com / Formateur@123");
        }
    }

    private void createSalles() {
        if (salleRepository.count() > 0) return;

        salleRepository.save(Salle.builder()
                .nom("Salle A101").capacite(20)
                .localisation("Bâtiment A - RDC")
                .equipements("Vidéoprojecteur, Tableau blanc, WiFi")
                .disponible(true).build());

        salleRepository.save(Salle.builder()
                .nom("Salle B202").capacite(15)
                .localisation("Bâtiment B - 2ème étage")
                .equipements("Écran interactif, WiFi")
                .disponible(true).build());

        salleRepository.save(Salle.builder()
                .nom("Salle Informatique").capacite(25)
                .localisation("Bâtiment C - RDC")
                .equipements("25 postes PC, Vidéoprojecteur, WiFi")
                .disponible(true).build());

        log.info("✅ 3 salles de test créées");
    }

    private void createFormationEtSeances() {
        Optional<User> formateurOpt = userRepository.findByEmail("formateur1@test.com");
        if (formateurOpt.isEmpty()) return;
        User formateur = formateurOpt.get();

        Salle salle = salleRepository.findAll().stream().findFirst().orElse(null);
        LocalDate today = LocalDate.now();

        // --- Formation 1 ---
        if (!formationRepository.existsByTitre("Développement Web Spring Boot & Angular")) {
            Formation f1 = formationRepository.save(Formation.builder()
                    .titre("Développement Web Spring Boot & Angular")
                    .description("Formation complète sur le développement d'applications web modernes avec Spring Boot et Angular.")
                    .duree(40)
                    .dateDebut(today)
                    .dateFin(today.plusMonths(2))
                    .capaciteMax(20)
                    .prix(BigDecimal.valueOf(300))
                    .statut(FormationStatut.EN_COURS)
                    .build());
            f1.getFormateurs().add(formateur);
            formationRepository.save(f1);

            seanceRepository.save(Seance.builder()
                    .titre("Introduction à Spring Boot")
                    .description("Démarrage rapide, configuration automatique, dépendances Maven/Gradle.")
                    .dateHeureDebut(LocalDateTime.of(today.plusDays(1), java.time.LocalTime.of(9, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusDays(1), java.time.LocalTime.of(12, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f1).salle(salle).formateur(formateur).build());

            seanceRepository.save(Seance.builder()
                    .titre("JPA & Hibernate")
                    .description("Modélisation de la persistance avec Spring Data JPA et Hibernate.")
                    .dateHeureDebut(LocalDateTime.of(today.plusDays(3), java.time.LocalTime.of(14, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusDays(3), java.time.LocalTime.of(17, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f1).salle(salle).formateur(formateur).build());

            seanceRepository.save(Seance.builder()
                    .titre("Angular - Composants & Routing")
                    .description("Architecture Angular : composants, services, routing et communication.")
                    .dateHeureDebut(LocalDateTime.of(today.plusDays(5), java.time.LocalTime.of(9, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusDays(5), java.time.LocalTime.of(12, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f1).salle(salle).formateur(formateur).build());

            log.info("✅ Formation 1 créée : Développement Web Spring Boot & Angular");
        }

        // --- Formation 2 ---
        if (!formationRepository.existsByTitre("Python pour la Data Science")) {
            Formation f2 = formationRepository.save(Formation.builder()
                    .titre("Python pour la Data Science")
                    .description("Maîtrisez Python pour l'analyse de données, la visualisation et l'apprentissage automatique avec NumPy, Pandas, Matplotlib et Scikit-learn.")
                    .duree(35)
                    .dateDebut(today.plusWeeks(1))
                    .dateFin(today.plusWeeks(1).plusMonths(2))
                    .capaciteMax(15)
                    .prix(BigDecimal.valueOf(200))
                    .statut(FormationStatut.PLANIFIEE)
                    .build());
            f2.getFormateurs().add(formateur);
            formationRepository.save(f2);

            seanceRepository.save(Seance.builder()
                    .titre("Python - Fondamentaux")
                    .description("Variables, types, structures de données, fonctions et programmation orientée objet en Python.")
                    .dateHeureDebut(LocalDateTime.of(today.plusWeeks(1).plusDays(1), java.time.LocalTime.of(9, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusWeeks(1).plusDays(1), java.time.LocalTime.of(12, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f2).salle(salle).formateur(formateur).build());

            seanceRepository.save(Seance.builder()
                    .titre("NumPy & Pandas")
                    .description("Manipulation et analyse de données tabulaires avec NumPy et Pandas.")
                    .dateHeureDebut(LocalDateTime.of(today.plusWeeks(1).plusDays(3), java.time.LocalTime.of(14, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusWeeks(1).plusDays(3), java.time.LocalTime.of(17, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f2).salle(salle).formateur(formateur).build());

            seanceRepository.save(Seance.builder()
                    .titre("Machine Learning avec Scikit-learn")
                    .description("Introduction aux modèles supervisés et non supervisés, évaluation des performances.")
                    .dateHeureDebut(LocalDateTime.of(today.plusWeeks(1).plusDays(5), java.time.LocalTime.of(9, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusWeeks(1).plusDays(5), java.time.LocalTime.of(12, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f2).salle(salle).formateur(formateur).build());

            log.info("✅ Formation 2 créée : Python pour la Data Science");
        }

        // --- Formation 3 ---
        if (!formationRepository.existsByTitre("Cybersécurité & Réseaux")) {
            Formation f3 = formationRepository.save(Formation.builder()
                    .titre("Cybersécurité & Réseaux")
                    .description("Comprendre les menaces informatiques, sécuriser les réseaux, maîtriser les outils de pentest et les bonnes pratiques de sécurité.")
                    .duree(30)
                    .dateDebut(today.plusWeeks(2))
                    .dateFin(today.plusWeeks(2).plusMonths(1))
                    .capaciteMax(12)
                    .prix(BigDecimal.valueOf(250))
                    .statut(FormationStatut.PLANIFIEE)
                    .build());
            f3.getFormateurs().add(formateur);
            formationRepository.save(f3);

            seanceRepository.save(Seance.builder()
                    .titre("Introduction à la Cybersécurité")
                    .description("Panorama des cybermenaces, OWASP Top 10, sécurité applicative et bonnes pratiques.")
                    .dateHeureDebut(LocalDateTime.of(today.plusWeeks(2).plusDays(1), java.time.LocalTime.of(9, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusWeeks(2).plusDays(1), java.time.LocalTime.of(12, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f3).salle(salle).formateur(formateur).build());

            seanceRepository.save(Seance.builder()
                    .titre("Sécurité des Réseaux")
                    .description("Protocoles réseau, firewalls, VPN, détection d'intrusion et audit de sécurité.")
                    .dateHeureDebut(LocalDateTime.of(today.plusWeeks(2).plusDays(3), java.time.LocalTime.of(14, 0)))
                    .dateHeureFin(LocalDateTime.of(today.plusWeeks(2).plusDays(3), java.time.LocalTime.of(17, 0)))
                    .statut(SeanceStatut.PLANIFIEE)
                    .formation(f3).salle(salle).formateur(formateur).build());

            log.info("✅ Formation 3 créée : Cybersécurité & Réseaux");
        }
    }

    private void createInscriptions() {
        List<Formation> formations = formationRepository.findAll();
        if (formations.isEmpty()) return;

        Formation formation = formations.stream()
                .filter(f -> "Développement Web Spring Boot & Angular".equals(f.getTitre()))
                .findFirst()
                .orElse(formations.get(0));

        userRepository.findByEmail("apprenant1@test.com").ifPresent(apprenant -> {
            if (!inscriptionRepository.existsByFormationIdAndApprenantId(formation.getId(), apprenant.getId())) {
                inscriptionRepository.save(Inscription.builder()
                        .formation(formation)
                        .apprenant(apprenant)
                        .statut(InscriptionStatut.ACCEPTEE)
                        .dateAcceptation(LocalDateTime.now())
                        .build());
            }
        });

        userRepository.findByEmail("sophie.martin@email.com").ifPresent(apprenant -> {
            if (!inscriptionRepository.existsByFormationIdAndApprenantId(formation.getId(), apprenant.getId())) {
                inscriptionRepository.save(Inscription.builder()
                        .formation(formation)
                        .apprenant(apprenant)
                        .statut(InscriptionStatut.ACCEPTEE)
                        .dateAcceptation(LocalDateTime.now())
                        .build());
            }
        });

        log.info("✅ 2 inscriptions ACCEPTEE créées pour les apprenants de test");
    }

    private void createPaiements() {
        if (paiementRepository.count() > 0) return;

        List<Formation> formations = formationRepository.findAll();
        if (formations.isEmpty()) return;

        Formation formation = formations.get(0);

        Optional<User> admin = userRepository.findByEmail("admin@formation.com");
        Optional<User> apprenant1 = userRepository.findByEmail("apprenant1@test.com");
        Optional<User> apprenant2 = userRepository.findByEmail("sophie.martin@email.com");

        if (admin.isEmpty() || apprenant1.isEmpty() || apprenant2.isEmpty()) return;

        // Paiement 1 — apprenant1, formation complète, EN_ATTENTE
        paiementRepository.save(Paiement.builder()
                .apprenant(apprenant1.get())
                .formation(formation)
                .modePaiement(ModePaiement.FORMATION)
                .montant(formation.getPrix())
                .remise(BigDecimal.ZERO)
                .montantNet(formation.getPrix())
                .datePaiement(LocalDate.now().minusDays(5))
                .statut(PaiementStatut.EN_ATTENTE)
                .enregistrePar(admin.get())
                .notes("Paiement formation complète")
                .build());

        // Paiement 2 — apprenant2, formation complète, VALIDE
        paiementRepository.save(Paiement.builder()
                .apprenant(apprenant2.get())
                .formation(formation)
                .modePaiement(ModePaiement.FORMATION)
                .montant(formation.getPrix())
                .remise(BigDecimal.valueOf(50))
                .montantNet(formation.getPrix().subtract(BigDecimal.valueOf(50)))
                .datePaiement(LocalDate.now().minusDays(10))
                .statut(PaiementStatut.VALIDE)
                .enregistrePar(admin.get())
                .dateValidation(LocalDateTime.now().minusDays(9))
                .validePar(admin.get())
                .notes("Remise fidélité -50 DT")
                .build());

        // Paiement 3 — apprenant1, par tranche 1/4, EN_ATTENTE
        BigDecimal tranche = formation.getPrix().divide(BigDecimal.valueOf(4), 2, java.math.RoundingMode.HALF_UP);
        paiementRepository.save(Paiement.builder()
                .apprenant(apprenant1.get())
                .formation(formation)
                .modePaiement(ModePaiement.TRANCHE)
                .montant(tranche)
                .remise(BigDecimal.ZERO)
                .montantNet(tranche)
                .datePaiement(LocalDate.now().minusDays(2))
                .statut(PaiementStatut.EN_ATTENTE)
                .trancheNumber(1)
                .enregistrePar(admin.get())
                .notes("Tranche 1/4")
                .build());

        // Paiement 4 — apprenant2, par séance, REJETE
        paiementRepository.save(Paiement.builder()
                .apprenant(apprenant2.get())
                .formation(formation)
                .modePaiement(ModePaiement.SEANCE)
                .montant(BigDecimal.valueOf(60))
                .remise(BigDecimal.ZERO)
                .montantNet(BigDecimal.valueOf(60))
                .datePaiement(LocalDate.now().minusDays(15))
                .statut(PaiementStatut.REJETE)
                .enregistrePar(admin.get())
                .commentaire("Montant incorrect")
                .build());

        log.info("✅ 4 paiements de test créés");
    }

    private void fixFormationPrices() {
        List<Formation> formations = formationRepository.findAll();
        if (formations.isEmpty()) return;

        int minDuree = formations.stream().mapToInt(Formation::getDuree).min().orElse(1);
        int maxDuree = formations.stream().mapToInt(Formation::getDuree).max().orElse(1);

        formations.forEach(f -> {
            BigDecimal prix;
            if (maxDuree == minDuree) {
                prix = BigDecimal.valueOf(675);
            } else {
                double ratio = (double)(f.getDuree() - minDuree) / (maxDuree - minDuree);
                prix = BigDecimal.valueOf(Math.round(550 + ratio * 250));
            }
            f.setPrix(prix);
            formationRepository.save(f);
            log.info("✅ Prix calculé : {} ({} h) → {} DT", f.getTitre(), f.getDuree(), prix);
        });
    }
}
