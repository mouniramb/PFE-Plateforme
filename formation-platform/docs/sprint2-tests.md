# Sprint 2 - Guide de test endpoints

## Prérequis
- PostgreSQL actif avec base `formation_db`
- Application backend lancée sur `:8080`
- Comptes disponibles: admin, formateur, apprenant

## Scénarios à tester

1. Créer une formation valide (ADMIN)
- Attendu: `201 Created`
- Vérifier email d'affectation envoyé aux formateurs.

2. Créer une formation avec dateFin <= dateDebut
- Attendu: `409 Conflict`
- Message: `La date de fin doit être après la date de début`

3. Inscription apprenant à une formation
- Attendu: `200 OK`, statut `EN_ATTENTE`
- Vérifier email apprenant + notification admin.

4. Double inscription même apprenant même formation
- Attendu: `409 Conflict`
- Message: `Vous êtes déjà inscrit à cette formation`

5. Acceptation inscription (ADMIN)
- Attendu: `200 OK`, statut `ACCEPTEE`
- Vérifier décrément des places restantes.

6. Rejet inscription (ADMIN)
- Attendu: `200 OK`, statut `REJETEE`
- Vérifier email avec motif.

7. Suppression formation avec inscriptions existantes
- Attendu: `409 Conflict`
- Message: `Impossible de supprimer une formation avec des inscrits`

8. Accès formateur à une formation non assignée
- Attendu: `403 Forbidden`
- Message: `Action non autorisée`
