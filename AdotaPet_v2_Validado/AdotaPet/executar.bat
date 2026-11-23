@echo off
cd /d "%~dp0"
java -jar "dist\AdotaPet.jar"
if errorlevel 1 (
  echo.
  echo Nao foi possivel executar. Verifique se Java esta instalado.
  pause
)
