@echo off
REM =========================================================
REM MY HOME - build and run (Windows)
REM Double-click this file, or run it from cmd/PowerShell.
REM =========================================================

echo Compiling MY HOME...
if not exist out mkdir out

setlocal enabledelayedexpansion
set SOURCES=
for /r src %%f in (*.java) do set SOURCES=!SOURCES! "%%f"

javac -d out !SOURCES!
if errorlevel 1 (
    echo.
    echo Compilation failed. Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Starting MY HOME...
echo.
java -cp out com.myhome.Main

pause
