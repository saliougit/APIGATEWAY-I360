@echo off
echo Nettoyage et compilation du projet...
call mvn clean install

if %errorlevel% neq 0 (
    echo Erreur lors de la compilation!
    pause
    exit /b %errorlevel%
)

echo.
echo Demarrage de l'application API Gateway...
echo.
call mvn spring-boot:run

pause
