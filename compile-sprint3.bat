@echo off
cd /d "c:\Users\mounira\Desktop\pfe\formation-platform"
echo Nettoyage et compilation Spring Boot...
echo.

REM Ajouter Java et Maven au PATH si nécessaire
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

REM Exécuter Maven via Java directement
echo ========================================
echo Compilation Maven...
echo ========================================
call mvnw.cmd clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESS - Compilation réussie!
    echo ========================================
    pause
) else (
    echo.
    echo ========================================
    echo BUILD FAILED - Erreur de compilation
    echo ========================================
    pause
)
