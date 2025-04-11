@echo off

echo [*] LuaCraft Build Script

IF NOT EXIST "gradlew" (
    echo [!] gradlew not found. Are you in the project root?
    exit /b 1
)

SET FRESH_BUILD=false
:parse_args
IF "%~1"=="" GOTO skip_args
IF "%~1"=="-fresh" GOTO set_fresh_build
IF "%~1"=="--fresh" GOTO set_fresh_build
SHIFT
GOTO parse_args

:set_fresh_build
SET FRESH_BUILD=true
SHIFT
GOTO parse_args

:skip_args

echo [*] Generating Lua API documentation...
call gradlew generateLuaDocs --console=plain >> build.log 2>&1

IF %ERRORLEVEL% NEQ 0 (
    echo [!] Skipping Lua docs generation: Task may not exist or failed.
) ELSE (
    echo [✓] Lua docs generated.
)

IF "%FRESH_BUILD%"=="true" (
    echo [*] Performing fresh build (clean + build)...
    call gradlew clean build --console=plain >> build.log 2>&1
) ELSE (
    echo [*] Performing normal build...
    call gradlew build --console=plain >> build.log 2>&1
)

IF %ERRORLEVEL% NEQ 0 (
    echo [!] Build failed. Showing last errors from build.log:
    echo ----------------------------------------
    FOR /F "tokens=*" %%A IN ('findstr /I "error:" build.log') DO (
        echo %%A
    )
    echo ----------------------------------------
    exit /b 1
)

echo [✓] Build complete. Artifacts are in build/libs/

REM Step 4: Show any warnings
SET WARNINGS=
SET DEPRECATIONS=
FOR /F "tokens=*" %%A IN ('findstr /I "warning:" build.log') DO SET WARNINGS=%%A
FOR /F "tokens=*" %%A IN ('findstr /I "deprecated" build.log') DO SET DEPRECATIONS=%%A

IF NOT "%WARNINGS%"=="" (
    echo [!] Build completed with warnings:
    echo ----------------------------------------
    echo %WARNINGS%
    echo ----------------------------------------
) ELSE (
    echo [✓] No warnings or deprecations found.
)

IF NOT "%DEPRECATIONS%"=="" (
    echo [!] Build completed with deprecations:
    echo ----------------------------------------
    echo %DEPRECATIONS%
    echo ----------------------------------------
)
