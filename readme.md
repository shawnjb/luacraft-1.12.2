# still has issues

currently a work in progress

im doing my best here, help is appreciated

---

set up the project for vscode (using the eclipse method)

```
./gradlew cleanEclipse eclipse
```

don't forget to set up the wrapper

```
gradle wrapper --gradle-version 4.9
```

then make sure you use the build script depending on your system

**linux**: `./build.sh -fresh`

**windows**: `./build.ps1 -fresh`