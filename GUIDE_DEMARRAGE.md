# 🚀 Guide de Démarrage - Sprint 1 & Sprint 2

## 📋 Prérequis

1. **PostgreSQL** : Base de données en cours d'exécution
2. **Java 17+** : JDK installé et dans le PATH
3. **Node.js 18+** : Pour Angular frontend
4. **Ports disponibles** : 8080 (backend), 4200 (frontend), 5432 (PostgreSQL)

---

## 🏗️ Configuration Base de Données

### Créer la base de données PostgreSQL
```sql
CREATE DATABASE formation_db;
```

### Vérifier la connexion
- Host: localhost
- Port: 5432
- Database: formation_db
- User: postgres
- Password: mounira

---

## 🎯 Démarrage Rapide

### Option 1 : Utiliser les fichiers Batch (Windows)

#### 1️⃣ Lancer le Backend
```bash
Double-cliquez sur: c:\Users\mounira\Desktop\pfe\run-backend.bat
```

Attendez le message : ✅ **Started FormationPlatformApplication**

#### 2️⃣ Dans un nouveau terminal, lancer le Frontend
```bash
Double-cliquez sur: c:\Users\mounira\Desktop\pfe\run-frontend.bat
```

Attendez le message : ✅ **Application bundle generation complete**

#### 3️⃣ Accéder à l'application
```
http://localhost:4200
```

---

### Option 2 : Via Terminal PowerShell/CMD

#### Backend:
```powershell
cd c:\Users\mounira\Desktop\pfe\formation-platform
.\mvnw.cmd spring-boot:run
```

#### Frontend (nouveau terminal):
```powershell
cd c:\Users\mounira\Desktop\pfe\frontend
npm start
```

---

## 🔐 Test des Identifiants

### Admin
- Email: `admin@formation.com`
- Password: `Admin@123`
- Rôle: ADMIN

### Apprenant
- Email: `apprenant1@test.com`
- Password: `Apprenant@123`
- Rôle: APPRENANT

**Note**: Les identifiants sont créés automatiquement par `DataInitializer.java` au démarrage.

---

## 🎨 Fonctionnalités Sprint 1 (Authentification)

✅ **Page de Login** : `http://localhost:4200/login`
- Connexion avec email/password
- Redirection automatique selon le rôle

✅ **Page d'Inscription** : `http://localhost:4200/register`
- Créer un compte apprenant
- Validation email unique

---

## 📚 Fonctionnalités Sprint 2 (Formations)

### Pour les Apprenants
1. **Catalogue** : `http://localhost:4200/catalogue`
   - Voir toutes les formations disponibles
   - Rechercher par mot-clé
   - S'inscrire à une formation

2. **Mes Inscriptions** : `http://localhost:4200/mes-inscriptions`
   - Voir ses inscriptions (EN_ATTENTE, ACCEPTEE, etc.)
   - Annuler ses inscriptions

### Pour les Admin
1. **Dashboard** : `http://localhost:4200/admin/dashboard`
   - Statistiques globales

2. **Formations** : `http://localhost:4200/admin/formations`
   - Créer/Modifier/Supprimer formations
   - Filtrer par statut
   - Voir les inscrits

3. **Inscriptions** : `http://localhost:4200/admin/inscriptions`
   - Voir les demandes en attente
   - Accepter/Rejeter avec motif

4. **Formateurs** : `http://localhost:4200/admin/formateurs`
   - CRUD formateurs

---

## 🛠️ Dépannage

### ❌ Le frontend affiche une page blanche

**Cause**: Le backend n'est pas lancé ou PostgreSQL n'est pas accessible

**Solution**:
1. ✅ Lancez PostgreSQL
2. ✅ Vérifiez que le backend affiche: `Started FormationPlatformApplication`
3. ✅ Vérifiez la console du navigateur (F12) pour voir les erreurs API
4. ✅ Rafraîchissez la page (F5)

### ❌ Erreur "Cannot connect to database"

**Cause**: PostgreSQL n'est pas accessible

**Solution**:
```sql
-- Vérifiez la base de données:
psql -U postgres -d formation_db
```

### ❌ Erreur "mvnw not found"

**Solution**:
Utilisez le fichier batch `run-backend.bat` ou lancez via l'IDE.

### ❌ Port 8080 déjà utilisé

**Solution**:
```powershell
# Trouver le processus qui utilise le port:
netstat -ano | findstr :8080

# Tuer le processus (remplacer PID):
taskkill /PID <PID> /F
```

---

## 📊 Vérification de l'Installation

### Test 1: Backend
```bash
curl http://localhost:8080/api/formations/catalogue
```
Devrait retourner un tableau JSON de formations.

### Test 2: Frontend
```bash
Accédez à http://localhost:4200 dans le navigateur
```

### Test 3: Authentification
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@formation.com","password":"Admin@123"}'
```
Devrait retourner un token JWT.

---

## 📁 Structure du Projet

```
formation-platform/
├── src/main/java/com/formation/     # Backend Spring Boot
│   ├── controller/                  # REST endpoints
│   ├── service/                     # Logique métier
│   ├── entity/                      # Entités JPA
│   ├── security/                    # JWT
│   └── config/                      # Configuration
├── src/main/resources/
│   └── application.properties        # Configuration BD
├── pom.xml                          # Dépendances Maven
└── mvnw.cmd                         # Maven wrapper

frontend/
├── src/app/
│   ├── components/                  # Composants Angular
│   ├── services/                    # Services HTTP
│   ├── guards/                      # Route guards
│   └── models/                      # Interfaces TypeScript
├── package.json                     # Dépendances npm
└── angular.json                     # Config Angular
```

---

## 🔗 URLs Importantes

| Entité | URL |
|--------|-----|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |
| API Docs | Voir sprint2-api.md |

---

## 📞 Support Rapide

**Si le frontend affiche rien:**
1. Ouvrir la console du navigateur (F12)
2. Aller dans l'onglet "Network"
3. Recharger la page
4. Vérifier les requêtes à `/api/` - elles doivent réussir avec 200 OK

**Si vous voyez des erreurs d'authentification:**
1. Vérifier que le backend est bien démarré
2. Vérifier les identifiants: `admin@formation.com` / `Admin@123`
3. Vider le localStorage du navigateur: F12 > Application > Clear All

---

## ✅ Checklist Démarrage

- [ ] PostgreSQL lancé et accessible
- [ ] Backend lancé (`./mvnw.cmd spring-boot:run`)
- [ ] Frontend lancé (`npm start`)
- [ ] Page accessible à http://localhost:4200
- [ ] Pouvez vous loguer avec admin@formation.com
- [ ] Pouvez voir le catalogue de formations
- [ ] Pouvez vous inscrire à une formation

**Tous les points ✅? Application prête pour les tests!**

