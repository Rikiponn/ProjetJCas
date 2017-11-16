@echo off

for %%a IN (*.ass) do (
echo --------------------------------------------------------------------
echo Fichier : %%a
echo --------------------------------------------------------------------
@echo on
Interp_MA\Exec\ima.exe %%a
@echo off
echo ---------------------------------------------------------------------
echo.
pause
echo.
)

echo Fin des tests
pause