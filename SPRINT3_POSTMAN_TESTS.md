# SPRINT 3 — COLLECTION POSTMAN POUR TESTS

**Objectif:** Valider tous les endpoints Sprint 3 avant deployment

---

## 🔐 SETUP INITIAL

### 1. Authentification
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "password123"
}

Response (201 Created):
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "nom": "Admin",
    "prenom": "User",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}

💾 Save token as {{ADMIN_TOKEN}} for all admin requests
```

### 2. Setup Headers pour tous les appels
```
Authorization: Bearer {{ADMIN_TOKEN}}
Content-Type: application/json
Accept: application/json
```

---

## 📝 PARTIE 1: GESTION DES SALLES

### 1.1 Créer une salle
```
POST http://localhost:8080/api/salles
Content-Type: application/json

{
  "nom": "Salle A - Informatique",
  "capacite": 30,
  "localisation": "Bâtiment A, Étage 2",
  "equipements": "Projector, WiFi, Tableaux blancs",
  "disponible": true
}

Expected: 201 Created
Response header: Location: /api/salles/{id}
Response body: SalleResponseDTO avec id généré
```

### 1.2 Récupérer toutes les salles (pagination)
```
GET http://localhost:8080/api/salles?page=0&size=10

Expected: 200 OK
Response:
{
  "content": [ ... ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0
}
```

### 1.3 Récupérer une salle spécifique
```
GET http://localhost:8080/api/salles/{id}

Expected: 200 OK
Response: SalleResponseDTO
```

### 1.4 Récupérer les salles disponibles
```
GET http://localhost:8080/api/salles/disponibles

Expected: 200 OK
Response: SalleResponseDTO[]
```

### 1.5 Vérifier disponibilité d'une salle
```
GET http://localhost:8080/api/salles/{salleId}/disponibilite?debut=2024-05-15T14:00&fin=2024-05-15T16:00&seanceId=1

Expected: 200 OK
Response:
{
  "disponible": true,
  "conflits": []
}

OR

{
  "disponible": false,
  "conflits": [
    {
      "seanceExistante": { ... },
      "messageConflit": "Conflit avec séance du 15-05 14h00"
    }
  ]
}
```

### 1.6 Mettre à jour une salle
```
PUT http://localhost:8080/api/salles/{id}
Content-Type: application/json

{
  "nom": "Salle A - Informatique (Updated)",
  "capacite": 35,
  "localisation": "Bâtiment A, Étage 2",
  "equipements": "Projector, WiFi, Tableaux blancs, Clim",
  "disponible": true
}

Expected: 200 OK
Response: SalleResponseDTO avec données mises à jour
```

### 1.7 Supprimer une salle
```
DELETE http://localhost:8080/api/salles/{id}

Expected: 200 OK
Response:
{
  "message": "Salle supprimée"
}

Error cases:
- 404 Not Found si salle n'existe pas
- 409 Conflict si salle a des séances
```

---

## 📅 PARTIE 2: GESTION DES SÉANCES

### 2.1 Détection des conflits avant création
```
POST http://localhost:8080/api/seances/conflits
Content-Type: application/json

{
  "titre": "Formation Angular Jour 1",
  "description": "Introduction à Angular 17",
  "dateHeureDebut": "2024-05-15T14:00:00",
  "dateHeureFin": "2024-05-15T16:00:00",
  "formationId": 1,
  "salleId": 1,
  "formateurId": 2,
  "statut": "PLANIFIEE"
}

Expected: 200 OK
Response: ConflitDTO[]

If no conflicts:
[]

If conflicts:
[
  {
    "seanceExistante": { ... },
    "messageConflit": "Conflit avec séance du 15/05 2024 14h00-16h00"
  }
]
```

### 2.2 Créer une séance (sans conflits)
```
POST http://localhost:8080/api/seances?forcer=false
Content-Type: application/json

{
  "titre": "Formation Angular Jour 1",
  "description": "Introduction à Angular 17",
  "dateHeureDebut": "2024-05-20T09:00:00",
  "dateHeureFin": "2024-05-20T11:00:00",
  "formationId": 1,
  "salleId": 1,
  "formateurId": 2,
  "statut": "PLANIFIEE"
}

Expected: 201 Created
Response: SeanceResponseDTO
{
  "id": 10,
  "titre": "Formation Angular Jour 1",
  "dateHeureDebut": "2024-05-20T09:00:00",
  "dateHeureFin": "2024-05-20T11:00:00",
  "statut": "PLANIFIEE",
  "dureeMinutes": 120,
  "formation": { "id": 1, "titre": "Formation Angular" },
  "salle": { "id": 1, "nom": "Salle A", ... },
  "formateur": { "id": 2, "nom": "Jean", "prenom": "Dupont", ... }
}
```

### 2.3 Créer une séance avec forçage (malgré conflits)
```
POST http://localhost:8080/api/seances?forcer=true
Content-Type: application/json

{
  "titre": "Formation Angular - Rattrapage",
  "description": "Session de rattrapage",
  "dateHeureDebut": "2024-05-15T14:00:00",
  "dateHeureFin": "2024-05-15T16:00:00",
  "formationId": 1,
  "salleId": 1,
  "formateurId": 2,
  "statut": "PLANIFIEE"
}

Expected: 201 Created (malgré conflit)
Response: SeanceResponseDTO
```

### 2.4 Récupérer les séances d'une formation
```
GET http://localhost:8080/api/seances/formation/{formationId}?page=0&size=10

Expected: 200 OK
Response: Page<SeanceResponseDTO>
```

### 2.5 Récupérer les séances d'un formateur
```
GET http://localhost:8080/api/seances/formateur/{formateurId}?page=0&size=10

Expected: 200 OK
Response: Page<SeanceResponseDTO>
```

### 2.6 Mettre à jour une séance
```
PUT http://localhost:8080/api/seances/{id}
Content-Type: application/json

{
  "titre": "Formation Angular Jour 1 (Updated)",
  "description": "Introduction à Angular 17 (Updated)",
  "dateHeureDebut": "2024-05-20T10:00:00",
  "dateHeureFin": "2024-05-20T12:00:00",
  "formationId": 1,
  "salleId": 1,
  "formateurId": 2,
  "statut": "PLANIFIEE"
}

Expected: 200 OK
Response: SeanceResponseDTO mis à jour
```

### 2.7 Changer le statut d'une séance
```
PUT http://localhost:8080/api/seances/{id}/statut
Content-Type: application/json

{
  "statut": "EN_COURS"
}

Expected: 200 OK
Response: SeanceResponseDTO

Valid transitions:
- PLANIFIEE → EN_COURS
- EN_COURS → TERMINEE
- PLANIFIEE → ANNULEE
```

### 2.8 Supprimer une séance
```
DELETE http://localhost:8080/api/seances/{id}

Expected: 200 OK
Response:
{
  "message": "Séance supprimée"
}

Note: Impossible si statut EN_COURS ou TERMINEE
```

---

## ✅ PARTIE 3: GESTION DES PRESENCES

### 3.1 Enregistrer une presence (simple)
```
POST http://localhost:8080/api/presences
Content-Type: application/json
Authorization: Bearer {{FORMATEUR_TOKEN}}

{
  "seanceId": 10,
  "apprenantId": 5,
  "statut": "PRESENT",
  "commentaire": "Present"
}

Expected: 201 Created
Response: PresenceResponseDTO
```

### 3.2 Enregistrer les presences en bulk
```
POST http://localhost:8080/api/presences/bulk
Content-Type: application/json
Authorization: Bearer {{FORMATEUR_TOKEN}}

{
  "seanceId": 10,
  "presences": [
    {
      "apprenantId": 5,
      "statut": "PRESENT",
      "commentaire": ""
    },
    {
      "apprenantId": 6,
      "statut": "ABSENT",
      "commentaire": "Justifié"
    },
    {
      "apprenantId": 7,
      "statut": "RETARD",
      "commentaire": "Arrivé 15 min tard"
    },
    {
      "apprenantId": 8,
      "statut": "EXCUSE",
      "commentaire": "Maladie"
    }
  ]
}

Expected: 201 Created
Response: PresenceResponseDTO[]
```

### 3.3 Récupérer les presences d'une séance
```
GET http://localhost:8080/api/presences/seance/{seanceId}
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: PresenceResponseDTO[]
```

### 3.4 Récupérer les presences d'un apprenant
```
GET http://localhost:8080/api/presences/apprenant/{apprenantId}
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: PresenceResponseDTO[]
```

### 3.5 Récupérer les presences d'un apprenant par formation
```
GET http://localhost:8080/api/presences/apprenant/{apprenantId}/formation/{formationId}
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: PresenceResponseDTO[]
```

### 3.6 Calculer le taux de présence
```
GET http://localhost:8080/api/presences/apprenant/{apprenantId}/formation/{formationId}/taux
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: TauxPresenceDTO
{
  "tauxPresence": 0.85,
  "seancesAssistees": 8,
  "totalSeances": 10
}
```

### 3.7 Récupérer mes presences (apprenant)
```
GET http://localhost:8080/api/presences/mes-presences
Authorization: Bearer {{APPRENANT_TOKEN}}

Expected: 200 OK
Response: PresenceResponseDTO[]
```

---

## 📊 PARTIE 4: GESTION DES NOTES

### 4.1 Enregistrer une note (simple)
```
POST http://localhost:8080/api/notes
Content-Type: application/json
Authorization: Bearer {{FORMATEUR_TOKEN}}

{
  "seanceId": 10,
  "apprenantId": 5,
  "valeur": 18.5,
  "coefficient": 2.0,
  "typeEvaluation": "EXAMEN",
  "commentaire": "Excellent travail"
}

Expected: 201 Created
Response: NoteResponseDTO
```

### 4.2 Enregistrer les notes en bulk
```
POST http://localhost:8080/api/notes/bulk
Content-Type: application/json
Authorization: Bearer {{FORMATEUR_TOKEN}}

{
  "seanceId": 10,
  "notes": [
    {
      "apprenantId": 5,
      "valeur": 18.5,
      "coefficient": 2.0,
      "typeEvaluation": "EXAMEN",
      "commentaire": "Excellent"
    },
    {
      "apprenantId": 6,
      "valeur": 14.0,
      "coefficient": 2.0,
      "typeEvaluation": "EXAMEN",
      "commentaire": "Bon"
    },
    {
      "apprenantId": 7,
      "valeur": 8.5,
      "coefficient": 2.0,
      "typeEvaluation": "EXAMEN",
      "commentaire": "Peut mieux faire"
    },
    {
      "apprenantId": 8,
      "valeur": 16.0,
      "coefficient": 2.0,
      "typeEvaluation": "EXAMEN",
      "commentaire": "Très bien"
    }
  ]
}

Expected: 201 Created
Response: NoteResponseDTO[]
```

### 4.3 Récupérer les notes d'une séance
```
GET http://localhost:8080/api/notes/seance/{seanceId}
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: NoteResponseDTO[]
```

### 4.4 Récupérer les notes d'un apprenant
```
GET http://localhost:8080/api/notes/apprenant/{apprenantId}
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: NoteResponseDTO[]
```

### 4.5 Récupérer les notes d'un apprenant par formation
```
GET http://localhost:8080/api/notes/apprenant/{apprenantId}/formation/{formationId}
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: NoteResponseDTO[]
```

### 4.6 Calculer la moyenne
```
GET http://localhost:8080/api/notes/apprenant/{apprenantId}/formation/{formationId}/moyenne
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: MoyenneDTO
{
  "moyenne": 14.3,
  "totalNotes": 4
}
```

### 4.7 Récupérer mes notes (apprenant)
```
GET http://localhost:8080/api/notes/mes-notes
Authorization: Bearer {{APPRENANT_TOKEN}}

Expected: 200 OK
Response: NoteResponseDTO[]
```

---

## 📈 PARTIE 5: SUIVI PÉDAGOGIQUE

### 5.1 Récupérer le suivi d'un apprenant (admin/formateur)
```
GET http://localhost:8080/api/suivi/apprenant/{apprenantId}/formation/{formationId}
Authorization: Bearer {{ADMIN_TOKEN}} or {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: StatistiquesApprenantDTO
{
  "apprenantId": 5,
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "formationId": 1,
  "formationTitre": "Formation Angular",
  "totalSeances": 10,
  "seancesAssistees": 8,
  "tauxPresence": 0.80,
  "moyenneGenerale": 14.3,
  "detailNotes": [ ... ],
  "detailPresences": [ ... ]
}
```

### 5.2 Récupérer le suivi de toute une formation
```
GET http://localhost:8080/api/suivi/formation/{formationId}
Authorization: Bearer {{ADMIN_TOKEN}} or {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: StatistiquesApprenantDTO[]
```

### 5.3 Récupérer mon suivi (apprenant)
```
GET http://localhost:8080/api/suivi/mon-suivi
Authorization: Bearer {{APPRENANT_TOKEN}}

Expected: 200 OK
Response: StatistiquesApprenantDTO[] (toutes formations)
```

---

## 📋 PARTIE 6: PLANNING

### 6.1 Récupérer le planning admin
```
GET http://localhost:8080/api/planning/admin?debut=2024-05-13&fin=2024-05-19
Authorization: Bearer {{ADMIN_TOKEN}}

Expected: 200 OK
Response: PlanningDTO
{
  "dateDebut": "2024-05-13T00:00:00",
  "dateFin": "2024-05-19T23:59:59",
  "seances": [
    { ... seances du 13 au 19 mai ... }
  ]
}
```

### 6.2 Récupérer le planning d'un formateur
```
GET http://localhost:8080/api/planning/formateur/{formateurId}?debut=2024-05-13&fin=2024-05-19
Authorization: Bearer {{ADMIN_TOKEN}} or {{FORMATEUR_TOKEN}}

Expected: 200 OK
Response: PlanningDTO (seances du formateur seulement)
```

### 6.3 Récupérer le planning d'un apprenant
```
GET http://localhost:8080/api/planning/apprenant/{apprenantId}?debut=2024-05-13&fin=2024-05-19
Authorization: Bearer {{ADMIN_TOKEN}} or {{APPRENANT_TOKEN}}

Expected: 200 OK
Response: PlanningDTO (seances de l'apprenant seulement)
```

### 6.4 Récupérer mon planning
```
GET http://localhost:8080/api/planning/mon-planning?debut=2024-05-13&fin=2024-05-19
Authorization: Bearer {{FORMATEUR_TOKEN}} or {{APPRENANT_TOKEN}}

Expected: 200 OK
Response: PlanningDTO (planning personnel)
```

---

## 🔒 PARTIE 7: TESTS DE SÉCURITÉ

### 7.1 Admin ne peut pas créer avec token formateur
```
POST http://localhost:8080/api/salles
Authorization: Bearer {{FORMATEUR_TOKEN}}

Expected: 403 Forbidden
Response:
{
  "error": "Accès refusé",
  "message": "Vous n'avez pas la permission d'accéder à cette ressource"
}
```

### 7.2 Apprenant ne peut pas accéder aux presences d'autrui
```
GET http://localhost:8080/api/presences/apprenant/{OTHER_APPRENANT_ID}
Authorization: Bearer {{APPRENANT_TOKEN}}

Expected: 403 Forbidden OR Service filters to own data
```

### 7.3 JWT token expiré retourne 401
```
GET http://localhost:8080/api/suivi/mon-suivi
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...EXPIRED_TOKEN

Expected: 401 Unauthorized
Response:
{
  "error": "Unauthorized",
  "message": "Token invalide ou expiré"
}
```

### 7.4 Pas de token retourne 401
```
GET http://localhost:8080/api/suivi/mon-suivi
(pas d'Authorization header)

Expected: 401 Unauthorized
```

---

## ✅ CHECKLIST DE VALIDATION FINALE

- [ ] Tous les endpoints 2xx/3xx retournent les bons DTOs
- [ ] Tous les endpoints 4xx retournent les bons codes d'erreur
- [ ] Les paramètres de pagination fonctionnent (?page=0&size=10)
- [ ] Les opérations bulk sont atomiques (tout ou rien)
- [ ] Les conflits sont correctement détectés et rapportés
- [ ] Les calculs de moyenne et taux sont exacts
- [ ] La sécurité est respectée (roles, JWT, parametres)
- [ ] Les dates ISO8601 sont correctement parsées
- [ ] Les enums sont correctement sérialisés/désérialisés
- [ ] Les relations imbriquées (formation, salle, formateur) sont complètes
- [ ] Les messages d'erreur sont informatifs
- [ ] Les timestamps (dateCreation, dateModification) sont générés côté serveur

---

**Total endpoints à tester:** 30+  
**Cas d'erreur:** 10+  
**Cas de sécurité:** 4+  
**Temps estimé:** 30-45 minutes

