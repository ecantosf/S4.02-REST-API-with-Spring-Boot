@echo off
REM Manage all three projects from root directory

if "%1"=="" (
    echo Usage: manage-projects.bat {build^|run^|test^|clean^|status}
    echo.
    echo Commands:
    echo   build   - Build all projects (skip tests)
    echo   run     - Run all projects in separate windows
    echo   test    - Run tests for all projects
    echo   clean   - Clean all projects
    echo   status  - Show git status
    exit /b 1
)

set COMMAND=%1

echo ========================================
echo Managing Fruit API Projects
echo ========================================

for %%p in (level1-fruit-api-h2 level2-fruit-api-mysql level3-fruit-order-api) do (
    echo.
    echo [%%p]
    cd %%p
    
    if "%COMMAND%"=="build" (
        echo Building...
        call mvn clean package -DskipTests
    ) else if "%COMMAND%"=="run" (
        echo Starting...
        start "%%p" cmd /k "mvn spring-boot:run"
    ) else if "%COMMAND%"=="test" (
        echo Testing...
        call mvn test
    ) else if "%COMMAND%"=="clean" (
        echo Cleaning...
        call mvn clean
    ) else if "%COMMAND%"=="status" (
        echo Git status from root only
    )
    
    cd ..
)

echo.
echo ========================================
echo Operation %COMMAND% completed!
echo ========================================