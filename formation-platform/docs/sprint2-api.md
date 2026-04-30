# Sprint 2 - Documentation API Backend

## Base URL
`http://localhost:8080/api`

## Authentification
Ajouter le header JWT:
`Authorization: Bearer <token>`

## Endpoints Formations

### POST /formations [ADMIN]
Créer une formation.

Corps JSON:
```json
{
  "titre": "Spring Boot Avance",
  "description": "Formation approfondie",
  "duree": 24,
  "dateDebut": "2026-05-01",
  "dateFin": "2026-05-10",
  "capaciteMax": 20,
  "prix": 1200.00,
  "statut": "PLANIFIEE",
  "formateurIds": [2, 5]
}
```

### PUT /formations/{id} [ADMIN]
Mettre à jour une formation.

### DELETE /formations/{id} [ADMIN]
Supprimer une formation (si aucune inscription).

### GET /formations?statut=PLANIFIEE&page=0&size=10 [ADMIN]
Lister les formations (pagination + filtre statut).

### GET /formations/catalogue [ALL]
Catalogue filtrable (statut/date/prix/capacité):
- `statut`
- `dateDebutMin`
- `dateFinMax`
- `prixMin`
- `prixMax`
- `capaciteMin`

### GET /formations/{id} [ALL]
Détails d'une formation.

### GET /formations/formateur/{formateurId} [ADMIN/FORMATEUR]
Lister les formations d'un formateur.

### GET /formations/search?keyword=spring [ALL]
Recherche par mot-clé.

## Endpoints Inscriptions

### POST /inscriptions/s-inscrire [APPRENANT]
Créer une demande d'inscription (EN_ATTENTE).

Corps JSON:
```json
{
  "formationId": 1
}
```

### PUT /inscriptions/{id}/accepter [ADMIN]
Accepter une inscription.

### PUT /inscriptions/{id}/rejeter [ADMIN]
Rejeter une inscription.

Corps JSON:
```json
{
  "motif": "Capacite atteinte"
}
```

### GET /inscriptions/en-attente [ADMIN]
Lister les inscriptions EN_ATTENTE.

### GET /inscriptions/formation/{formationId} [ADMIN/FORMATEUR]
Lister les apprenants ACCEPTES d'une formation.

### GET /inscriptions/apprenant/{apprenantId} [APPRENANT/ADMIN]
Lister les inscriptions d'un apprenant.

### DELETE /inscriptions/{id}/annuler [APPRENANT]
Annuler sa propre inscription.

## Codes Erreur Metier
- `Formation non trouvée`
- `Vous êtes déjà inscrit à cette formation`
- `Aucune place disponible pour cette formation`
- `La date de fin doit être après la date de début`
- `Données de formation invalides (capacité > 0, prix >= 0)`
- `Impossible de supprimer une formation avec des inscrits`
- `Inscription non trouvée`
- `Action non autorisée`
