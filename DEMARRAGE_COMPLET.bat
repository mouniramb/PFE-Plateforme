@echo off
REM Script de vérification et démarrage - Formation Platform

echo.
echo ============================================
echo   VERIFICATION - Formation Platform
echo ============================================
echo.

REM Vérifier Java
echo [1/3] Vérification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java n'est pas installé ou pas dans le PATH
    echo    Veuillez installer Java 17+ depuis https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
) else (
    echo ✅ Java détecté
)

echo.

REM Vérifier Node.js
echo [2/3] Vérification de Node.js...
node --version >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Node.js n'est pas détecté (optionnel si npm start ne fonctionne pas)
) else (
    echo ✅ Node.js détecté
)

echo.

REM Vérifier PostgreSQL
echo [3/3] Vérification de PostgreSQL...
echo   PostgreSQL doit s'exécuter sur localhost:5432
echo   Base de données: formation_db
echo   Utilisateur: postgres
echo   Mot de passe: mounira
echo.
echo   Si PostgreSQL n'est pas en cours d'exécution:
echo   - Windows: Démarrez depuis Services (PostgreSQL-x64-XX)
echo   - Ou exécutez: pg_ctl -D "C:\Program Files\PostgreSQL\xx\data" start
echo.

echo.
echo ============================================
echo   DEMARRAGE - Formation Platform
echo ============================================
echo.

REM Créer deux fenêtres de terminal
echo [1/2] Démarrage du Backend (Port 8080)...
cd /d "c:\Users\mounira\Desktop\pfe\formation-platform"
start "Backend - Formation Platform" cmd /k "echo Démarrage du backend... && echo. && call mvnw.cmd spring-boot:run"

echo [2/2] Démarrage du Frontend (Port 4200)...
timeout /t 5
cd /d "c:\Users\mounira\Desktop\pfe\frontend"
start "Frontend - Formation Platform" cmd /k "echo Démarrage du frontend... && echo. && call npm start"

echo.
echo ✅ Démarrage lancé!
echo.
echo   Backend: http://localhost:8080
echo   Frontend: http://localhost:4200
echo.
echo   Identifiants de test:
echo   - Admin: admin@formation.com / Admin@123
echo   - Apprenant: apprenant1@test.com / Apprenant@123
echo   - Formateur: formateur1@test.com / Formateur@123
echo.
echo   Attendez 30-60 secondes que les deux services se lancent...
echo.

pause
