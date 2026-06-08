@echo off
REM Maven Wrapper - Simple Batch Version without PowerShell dependency

setlocal enabledelayedexpansion

set MAVEN_HOME=%~dp0.mvn
set MAVEN_WRAPPER_JAR=%MAVEN_HOME%\wrapper\maven-wrapper.jar

if not exist "%MAVEN_WRAPPER_JAR%" (
    echo Maven wrapper JAR not found at: %MAVEN_WRAPPER_JAR%
    exit /b 1
)

REM Get the Maven version to download
for /f "tokens=*" %%i in (%MAVEN_HOME%\wrapper\maven-wrapper.properties) do (
    if "%%i"=="mavenVersion=3.9.6" set MAVEN_VERSION=3.9.6
    if "%%i"=="mavenVersion=3.9.5" set MAVEN_VERSION=3.9.5
)

if not defined MAVEN_VERSION (
    echo Could not determine Maven version from properties
    exit /b 1
)

REM Set JAVA_HOME if not already set
if not defined JAVA_HOME (
    set JAVA_HOME=C:\Program Files\Java\jdk-17
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Java not found. Please set JAVA_HOME environment variable
    exit /b 1
)

REM Run Maven Wrapper
"%JAVA_HOME%\bin\java.exe" -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

exit /b %ERRORLEVEL%
