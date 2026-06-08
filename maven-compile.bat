@echo off
REM Compilation avec Maven Wrapper - chemin absolu

setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-17
set MAVEN_OPTS=-Xmx1024m
set PROJECT_DIR=c:\Users\mounira\Desktop\pfe\formation-platform

echo.
echo ========================================
echo Verification de Java...
echo ========================================
"%JAVA_HOME%\bin\java.exe" -version

echo.
echo ========================================
echo Compilation Spring Boot - Sprint 3
echo ========================================
echo.

set WRAPPER_JAR=%PROJECT_DIR%\.mvn\wrapper\maven-wrapper.jar

if not exist "%WRAPPER_JAR%" (
    echo Erreur: Maven Wrapper JAR non trouvé
    echo Chemin: %WRAPPER_JAR%
    exit /b 1
)

echo Chemin Maven Wrapper: %WRAPPER_JAR%
echo Repertoire: %PROJECT_DIR%
echo.

cd /d "%PROJECT_DIR%"

"%JAVA_HOME%\bin\java.exe" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain clean compile

exit /b %ERRORLEVEL%
