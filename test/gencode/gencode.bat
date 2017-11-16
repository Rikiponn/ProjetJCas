@echo off

set build=g
set /p build=Pas a pas (p) ou global (g) ? (default to g) :

for %%a IN (*.cas) do (
echo --------------------------------------------------------------------
echo Fichier : %%a
echo --------------------------------------------------------------------
cd ..\..\classes
@echo on
java -cp .;..\lib\java-cup-11a-runtime.jar fr.esisar.compilation.gencode.JCasc ../test/gencode/%%a
@echo off
xcopy *.ass ..\test\gencode\*.ass /q /c /y
del *.ass
cd ..\test\gencode
echo ---------------------------------------------------------------------
echo. 
if %build% == p pause

echo.
)

echo Fin de la compilation
pause
