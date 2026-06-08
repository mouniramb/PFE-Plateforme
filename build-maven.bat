@echo off
REM Script pour compiler le projet sans mvnw (contournement)

cd /d "c:\Users\mounira\Desktop\pfe\formation-platform"

REM Définir JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

REM Télécharger Maven si nécessaire
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
set MAVEN_HOME=%USERPROFILE%\maven-3.9.6
set MAVEN_BIN=%MAVEN_HOME%\bin

if not exist "%MAVEN_BIN%\mvn.cmd" (
    echo Téléchargement de Maven 3.9.6...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%MAVEN_URL%', '%USERPROFILE%\maven.zip')"
    
    echo Extraction de Maven...
    powershell -Command "Expand-Archive -Path '%USERPROFILE%\maven.zip' -DestinationPath '%USERPROFILE%'"
    
    ren "%USERPROFILE%\apache-maven-3.9.6" "maven-3.9.6"
    del "%USERPROFILE%\maven.zip"
)

REM Compiler le projet
echo.
echo ========================================
echo Compilation du projet Spring Boot...
echo ========================================
echo.

call "%MAVEN_BIN%\mvn.cmd" clean compile %*

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESS
    echo ========================================
) else (
    echo.
    echo ========================================
    echo BUILD FAILED
    echo ========================================
)

exit /b %ERRORLEVEL%
