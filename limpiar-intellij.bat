@echo off
echo ========================================
echo   Limpieza Rapida de IntelliJ IDEA
echo ========================================
echo.

echo [1/4] Limpiando archivos de compilacion...
if exist "target" rmdir /s /q "target"
if exist "out" rmdir /s /q "out"
echo ✓ Limpieza de compilacion completada
echo.

echo [2/4] Limpiando archivos de Maven...
if exist ".mvn" (
    cd .mvn
    if exist "wrapper" rmdir /s /q "wrapper"
    cd ..
)
echo ✓ Limpieza de Maven completada
echo.

echo [3/4] Limpiando cache de IntelliJ...
if exist ".idea\workspace.xml" del /q ".idea\workspace.xml"
if exist ".idea\tasks.xml" del /q ".idea\tasks.xml"
if exist ".idea\dictionaries" rmdir /s /q ".idea\dictionaries"
echo ✓ Limpieza de cache completada
echo.

echo [4/4] Recompilando proyecto con Maven...
call mvn clean compile -q
if %ERRORLEVEL% EQU 0 (
    echo ✓ Compilacion exitosa
) else (
    echo ✗ Error en la compilacion
)
echo.

echo ========================================
echo   Limpieza completada!
echo ========================================
echo.
echo Ahora en IntelliJ IDEA:
echo 1. File -^> Invalidate Caches... -^> Invalidate and Restart
echo 2. Maven -^> Reload Project
echo 3. Build -^> Rebuild Project
echo.
pause

