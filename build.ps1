
$RED = "Red"
$GREEN = "Green"
$YELLOW = "Yellow"
$NC = "White"

Write-Host "[*] LuaCraft Build Script" -ForegroundColor $GREEN

if (-Not (Test-Path "./gradlew")) {
    Write-Host "[!] gradlew not found. Are you in the project root?" -ForegroundColor $RED
    exit 1
}

$FRESH_BUILD = $false

foreach ($arg in $args) {
    if ($arg -eq "-fresh" -or $arg -eq "--fresh") {
        $FRESH_BUILD = $true
    }
}

Clear-Content -Path build.log -ErrorAction SilentlyContinue

Write-Host "[*] Generating Lua API documentation..." -ForegroundColor $GREEN
./gradlew generateLuaDocs --console=plain --stacktrace --info | Out-File -Append build.log

if ($LASTEXITCODE -ne 0) {
    Write-Host "[!] Skipping Lua docs generation: Task may not exist or failed." -ForegroundColor $YELLOW
} else {
    Write-Host "[OK] Lua docs generated." -ForegroundColor $GREEN
}

if ($FRESH_BUILD) {
    Write-Host "[*] Performing fresh build (clean + build)..." -ForegroundColor $GREEN
    ./gradlew clean build --console=plain | Out-File -Append build.log
} else {
    Write-Host "[*] Performing normal build..." -ForegroundColor $GREEN
    ./gradlew build --console=plain | Out-File -Append build.log
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "[!] Build failed. Showing last errors from build.log:" -ForegroundColor $RED
    Write-Host "----------------------------------------" -ForegroundColor $YELLOW
    Get-Content build.log | Select-String -Pattern "error:" -Context 5,5 | ForEach-Object { Write-Host $_.Line }
    Write-Host "----------------------------------------" -ForegroundColor $YELLOW
    exit 1
}

Write-Host "[OK] Build complete. Artifacts are in build/libs/" -ForegroundColor $GREEN

$WARNINGS = Get-Content build.log | Select-String -Pattern "warning:"
$DEPRECATIONS = Get-Content build.log | Select-String -Pattern "deprecated"

if ($WARNINGS -or $DEPRECATIONS) {
    Write-Host "[!] Build completed with warnings:" -ForegroundColor $YELLOW
    Write-Host "----------------------------------------" -ForegroundColor $YELLOW
    $WARNINGS | ForEach-Object { Write-Host $_.Line -ForegroundColor $YELLOW }
    $DEPRECATIONS | ForEach-Object { Write-Host $_.Line -ForegroundColor $YELLOW }
    Write-Host "----------------------------------------" -ForegroundColor $YELLOW
} else {
    Write-Host "[OK] No warnings or deprecations found." -ForegroundColor $GREEN
}
