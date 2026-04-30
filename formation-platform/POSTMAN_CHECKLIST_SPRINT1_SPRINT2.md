# Postman Checklist - Sprint 1 + Sprint 2

Ce document valide la compatibilite entre:
- Sprint 1: auth JWT + gestion formateurs
- Sprint 2: formations + inscriptions

## 1) Preparation Postman

Creer un Environment avec ces variables:
- `baseUrl` = `http://localhost:8080`
- `adminEmail` = `admin@formation.com`
- `adminPassword` = `Admin@123`
- `apprenantEmail` = `apprenant1@test.com`
- `apprenantPassword` = `Apprenant@123`
- `adminToken` = *(vide)*
- `apprenantToken` = *(vide)*
- `apprenantId` = *(vide)*
- `formateurId` = *(vide)*
- `formationId` = *(vide)*
- `inscriptionId` = *(vide)*

Headers JSON par defaut:
- `Content-Type: application/json`

Header auth pour endpoints securises:
- `Authorization: Bearer {{adminToken}}` ou `Bearer {{apprenantToken}}`

## 2) Sprint 1 - Auth et Utilisateurs

### 2.1 Login admin
- Methode: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:
```json
{
  "email": "{{adminEmail}}",
  "password": "{{adminPassword}}"
}
```
- Attendu: `200 OK`
- Test script Postman:
```javascript
pm.test("200", function () { pm.response.to.have.status(200); });
const json = pm.response.json();
pm.collectionVariables.set("adminToken", json.token);
```

### 2.2 Register apprenant
- Methode: `POST`
- URL: `{{baseUrl}}/api/auth/register`
- Body:
```json
{
  "nom": "Test",
  "prenom": "Apprenant",
  "email": "{{apprenantEmail}}",
  "password": "{{apprenantPassword}}"
}
```
- Attendu: `201 Created` (ou `409 Conflict` si deja existant)

### 2.3 Login apprenant
- Methode: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:
```json
{
  "email": "{{apprenantEmail}}",
  "password": "{{apprenantPassword}}"
}
```
- Attendu: `200 OK`
- Test script Postman:
```javascript
pm.test("200", function () { pm.response.to.have.status(200); });
const json = pm.response.json();
pm.collectionVariables.set("apprenantToken", json.token);
pm.collectionVariables.set("apprenantId", json.id);
```

### 2.4 Profil utilisateur connecte
- Methode: `GET`
- URL: `{{baseUrl}}/api/users/me`
- Header: `Authorization: Bearer {{adminToken}}`
- Attendu: `200 OK`

### 2.5 Creer formateur (admin)
- Methode: `POST`
- URL: `{{baseUrl}}/api/users/formateurs`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:
```json
{
  "nom": "Form",
  "prenom": "Ateur",
  "email": "formateur1@test.com"
}
```
- Attendu: `201 Created` (ou `409 Conflict` si email existe)
- Test script Postman (si 201):
```javascript
if (pm.response.code === 201) {
  const json = pm.response.json();
  pm.collectionVariables.set("formateurId", json.id);
}
```

### 2.6 Lister formateurs (admin)
- Methode: `GET`
- URL: `{{baseUrl}}/api/users/formateurs`
- Header: `Authorization: Bearer {{adminToken}}`
- Attendu: `200 OK`

## 3) Sprint 2 - Formations

### 3.1 Creer formation sans formateurs (cas corrige)
- Methode: `POST`
- URL: `{{baseUrl}}/api/formations`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:
```json
{
  "titre": "Java Spring Boot Avance",
  "description": "Cours complet Spring Boot JPA Security",
  "duree": 40,
  "dateDebut": "2026-05-01",
  "dateFin": "2026-06-01",
  "capaciteMax": 30,
  "prix": 500.0,
  "statut": "PLANIFIEE"
}
```
- Attendu: `201 Created`
- Test script Postman:
```javascript
pm.test("201", function () { pm.response.to.have.status(201); });
const json = pm.response.json();
pm.collectionVariables.set("formationId", json.id);
```

### 3.2 Mettre a jour formation en ajoutant un formateur (optionnel)
- Methode: `PUT`
- URL: `{{baseUrl}}/api/formations/{{formationId}}`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:
```json
{
  "titre": "Java Spring Boot Avance",
  "description": "Cours complet Spring Boot JPA Security",
  "duree": 40,
  "dateDebut": "2026-05-01",
  "dateFin": "2026-06-01",
  "capaciteMax": 30,
  "prix": 500.0,
  "statut": "PLANIFIEE",
  "formateurIds": [{{formateurId}}]
}
```
- Attendu: `200 OK`

### 3.3 Catalogue public
- Methode: `GET`
- URL: `{{baseUrl}}/api/formations/catalogue`
- Attendu: `200 OK` sans token

### 3.4 Recherche publique
- Methode: `GET`
- URL: `{{baseUrl}}/api/formations/search?keyword=Spring`
- Attendu: `200 OK` sans token

## 4) Sprint 2 - Inscriptions

### 4.1 Apprenant s'inscrit a une formation
- Methode: `POST`
- URL: `{{baseUrl}}/api/inscriptions/s-inscrire`
- Header: `Authorization: Bearer {{apprenantToken}}`
- Body:
```json
{
  "formationId": {{formationId}}
}
```
- Attendu: `200 OK`
- Test script Postman:
```javascript
pm.test("200", function () { pm.response.to.have.status(200); });
const json = pm.response.json();
pm.collectionVariables.set("inscriptionId", json.id);
```

### 4.2 Admin liste inscriptions en attente
- Methode: `GET`
- URL: `{{baseUrl}}/api/inscriptions/en-attente`
- Header: `Authorization: Bearer {{adminToken}}`
- Attendu: `200 OK`

### 4.3 Admin accepte inscription
- Methode: `PUT`
- URL: `{{baseUrl}}/api/inscriptions/{{inscriptionId}}/accepter`
- Header: `Authorization: Bearer {{adminToken}}`
- Attendu: `200 OK`

### 4.4 Consulter inscriptions d'un apprenant
- Methode: `GET`
- URL: `{{baseUrl}}/api/inscriptions/apprenant/{{apprenantId}}`
- Header: `Authorization: Bearer {{apprenantToken}}`
- Attendu: `200 OK`

### 4.5 Annuler inscription (apprenant)
- Methode: `DELETE`
- URL: `{{baseUrl}}/api/inscriptions/{{inscriptionId}}/annuler`
- Header: `Authorization: Bearer {{apprenantToken}}`
- Attendu: `204 No Content`

## 5) Verifications de securite (important compatibilite Sprint 1)

Executer ces tests de non-regression:
- `GET /api/users/formateurs` sans token -> attendu `401` ou `403`
- `POST /api/formations` avec token apprenant -> attendu `403`
- `POST /api/inscriptions/s-inscrire` sans token -> attendu `401` ou `403`
- `GET /api/formations/catalogue` sans token -> attendu `200`

Si ces 4 points passent, la securite Sprint 1 et les ajouts Sprint 2 sont coherents.

## 6) Depannage rapide - Error connect ECONNREFUSED 127.0.0.1:8080

Cette erreur signifie que le serveur Spring Boot n'est pas demarre (ou pas sur 8080).

1. Demarrer PostgreSQL.
2. Demarrer le backend.

Option A (recommandee): lancer la classe principale depuis VS Code/IDE
- Classe: [formation-platform/src/main/java/com/formation/FormationPlatformApplication.java](formation-platform/src/main/java/com/formation/FormationPlatformApplication.java)

Option B (terminal local Windows)
- `mvnw.cmd spring-boot:run`

3. Verifier que le serveur ecoute sur le port 8080:
- `netstat -ano | findstr :8080`

4. Tester un endpoint public dans le navigateur/Postman:
- `GET http://localhost:8080/api/formations/catalogue`
- attendu: reponse HTTP (200), pas d'erreur de connexion.

5. Si l'application ne demarre pas, lire la premiere erreur dans les logs.
Cas frequents:
- PostgreSQL arrete ou mauvais identifiants dans [formation-platform/src/main/resources/application.properties](formation-platform/src/main/resources/application.properties)
- Port 8080 deja occupe par une autre application

## 7) Notes build (dans ce terminal outille)

Si votre terminal VS Code ne reconnait pas `mvn`, `cmd` ou `powershell`, ce probleme vient du shell outille ici, pas du code Java.
Sur votre machine, vous pouvez compiler avec:
- `mvnw.cmd -DskipTests compile`
- ou `mvnw.cmd spring-boot:run`
