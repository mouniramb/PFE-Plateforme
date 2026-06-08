@echo off
REM Compilation directe sans dépendre de PowerShell

setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
set MAVEN_OPTS=-Xmx1024m

cd /d "c:\Users\mounira\Desktop\pfe\formation-platform"

echo.
echo ========================================
echo Vérification de Java...
echo ========================================
"%JAVA_HOME%\bin\java.exe" -version

echo.
echo ========================================
echo Compilation Spring Boot - Sprint 3
echo ========================================
echo.

REM Essayer avec le wrapper Maven JAR directement
set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar

if exist "%WRAPPER_JAR%" (
    echo Utilisation du Maven Wrapper...
    "%JAVA_HOME%\bin\java.exe" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain clean compile
    goto :done
)

echo Fichier wrapper introuvable: %WRAPPER_JAR%
exit /b 1

:done
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESS - Compilation reussie
    echo ========================================
    pause
) else (
    echo.
    echo ========================================
    echo BUILD FAILED - Erreur lors de la compilation
    echo ========================================
    pause
)

exit /b %ERRORLEVEL%
