@echo off
for %%a IN (*.cas) do (
echo --------------------------------------------------------------------
echo Fichier : %%a
echo --------------------------------------------------------------------
cd ..\..\classes
@echo on
java -cp .;..\lib\java-cup-11a-runtime.jar fr.esisar.compilation.gencode.JCasc ../test/gencode/%%a
@echo off
xcopy *.ass ..\test\gencode\codeAssembleur\*.ass /q /c /y
del *.ass
cd ..\test\gencode
echo ---------------------------------------------------------------------
echo. 
pause
echo.
)

