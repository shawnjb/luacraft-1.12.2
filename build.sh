#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}[*] Generating Lua API documentation...${NC}"
./gradlew generateLuaDocs --console=plain

if [ $? -ne 0 ]; then
    echo -e "${RED}[!] Failed to generate Lua docs. Aborting.${NC}"
    exit 1
fi

echo -e "${GREEN}[*] Building project with LuaJ...${NC}"

# Ensure LuaJ is included by building the project
./gradlew clean build --console=plain

if [ $? -ne 0 ]; then
    echo -e "${RED}[!] Build failed. Make sure LuaJ is properly included in your build.gradle dependencies.${NC}"
    exit 1
fi

echo -e "${GREEN}[✓] Build complete with LuaJ compiled. Artifacts are in build/libs/${NC}"
