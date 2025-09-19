# Build Pipeline

This repository includes a GitHub Actions workflow for building and releasing the Nixie Clock Android app.

## Manual Build and Release

The build pipeline can be triggered manually from the GitHub Actions tab:

1. Go to the **Actions** tab in the GitHub repository
2. Select **Build and Release** workflow
3. Click **Run workflow**
4. Provide the required inputs:
   - **Version tag**: e.g., `v1.5.1` or `v2.0.0`
   - **Release name**: (optional) Custom name for the release

## What the Pipeline Does

When triggered, the workflow will:

1. **Setup Environment**: Configure Java 11 and Android SDK
2. **Generate Changelog**: Collect all commit messages since the last release
3. **Build APKs**: Create both debug and release versions
4. **Create Release**: Generate a GitHub release with:
   - Version tag you specified
   - Changelog with commit history
   - Debug APK attachment (~19.5 MB)
   - Release APK attachment (~17.8 MB)
   - Build artifacts (retained for 30 days)

## APK Outputs

- **Debug APK**: `nixieclock-{version}-debug.apk` - includes debugging symbols
- **Release APK**: `nixieclock-{version}-release.apk` - optimized and minified

## Requirements

The pipeline handles all dependencies automatically:
- Java 11 (Temurin distribution)
- Android SDK API level 30
- Gradle 6.7.1
- Build tools and dependencies

## Local Development

For local development, ensure you have:
- Java 11 installed and configured
- Android SDK with API level 30
- The `app/keystore.properties` file (created automatically by the pipeline)

Build commands:
```bash
# Debug build
./gradlew clean assembleDebug

# Release build  
./gradlew assembleRelease

# Run tests
./gradlew test
```