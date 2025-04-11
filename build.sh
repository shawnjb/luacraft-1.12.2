#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}[*] LuaCraft Build Script${NC}"

if [ ! -f "./gradlew" ]; then
    echo -e "${RED}[!] gradlew not found. Are you in the project root?${NC}"
    exit 1
fi

FRESH_BUILD=false
for arg in "$@"; do
    if [[ "$arg" == "-fresh" || "$arg" == "--fresh" ]]; then
        FRESH_BUILD=true
    fi
done

: > build.log

echo -e "${GREEN}[*] Generating Lua API documentation...${NC}"
./gradlew generateLuaDocs --console=plain >> build.log 2>&1

if [ $? -ne 0 ]; then
    echo -e "${YELLOW}[!] Skipping Lua docs generation: Task may not exist or failed.${NC}"
else
    echo -e "${GREEN}[✓] Lua docs generated.${NC}"
fi

if [ "$FRESH_BUILD" = true ]; then
    echo -e "${GREEN}[*] Performing fresh build (clean + build)...${NC}"
    ./gradlew clean build --console=plain >> build.log 2>&1
else
    echo -e "${GREEN}[*] Performing normal build...${NC}"
    ./gradlew build --console=plain >> build.log 2>&1
fi

if [ $? -ne 0 ]; then
    echo -e "${RED}[!] Build failed. Showing last errors from build.log:${NC}"
    echo -e "${YELLOW}----------------------------------------${NC}"
    grep -A 5 -B 5 "error:" build.log | tail -n 20
    echo -e "${YELLOW}----------------------------------------${NC}"
    exit 1
fi

echo -e "${GREEN}[✓] Build complete. Artifacts are in ${YELLOW}build/libs/${NC}"

# Step 4: Show any warnings
WARNINGS=$(grep -i "warning:" build.log || true)
DEPRECATIONS=$(grep -i "deprecated" build.log || true)

if [ -n "$WARNINGS" ] || [ -n "$DEPRECATIONS" ]; then
    echo -e "${YELLOW}[!] Build completed with warnings:${NC}"
    echo -e "${YELLOW}----------------------------------------${NC}"
    echo "$WARNINGS"
    echo "$DEPRECATIONS"
    echo -e "${YELLOW}----------------------------------------${NC}"
else
    echo -e "${GREEN}[✓] No warnings or deprecations found.${NC}"
fi
