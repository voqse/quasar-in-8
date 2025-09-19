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
   - **Include debug APK**: (optional) Generate debug APK with temporary keystore for testing

## Required GitHub Secrets

Configure these secrets in your repository settings for release builds:

- `KEYSTORE_PASSWORD`: Password for the release keystore
- `KEY_ALIAS`: Alias of the signing key in the keystore  
- `KEY_PASSWORD`: Password for the signing key
- `RELEASE_KEYSTORE`: Base64-encoded release keystore file (.jks)

To create the `RELEASE_KEYSTORE` secret:
```bash
# Encode your keystore file to base64
base64 -w 0 your-keystore.jks
# Copy the output and paste it as the RELEASE_KEYSTORE secret value
```

## What the Pipeline Does

When triggered, the workflow will:

1. **Setup Environment**: Configure Java 11 and Android SDK
2. **Setup Release Keystore**: Create keystore configuration from GitHub Secrets
3. **Generate Changelog**: Collect all commit messages since the last release
4. **Build Release APK**: Create optimized release version (~17.8 MB)
5. **Optional Debug APK**: If requested, generate temporary debug keystore and build debug APK
6. **Create Release**: Generate a GitHub release with:
   - Version tag you specified
   - Changelog with commit history
   - Release APK attachment (always included)
   - Debug APK attachment (only if requested)
   - Build artifacts (retained for 30 days)

## APK Outputs

- **Release APK**: `nixieclock-{version}-release.apk` - optimized and minified, signed with your release keystore
- **Debug APK**: `nixieclock-{version}-debug.apk` - (optional) includes debugging symbols, signed with temporary keystore

## Optimized Approach

**Release-First Strategy**: The pipeline focuses on release APK generation since that's what you need for distribution. Debug APKs are only generated when specifically requested for testing purposes.

**Temporary Debug Keystores**: When debug APK is requested, the pipeline generates a temporary debug keystore valid for 1 day, eliminating the need to store debug credentials.

**Minimal Secrets**: Only release keystore credentials need to be stored as GitHub Secrets, simplifying secret management.

## Usage

To create a new release:
1. Configure 4 GitHub Secrets with your release keystore credentials (one-time setup)
2. Go to Actions tab → "Build and Release" workflow
3. Click "Run workflow" 
4. Enter version tag (e.g., `v1.6.0`)
5. Optionally check "Include debug APK" if you need it for testing
6. Pipeline will build release APK (and debug APK if requested) and create release automatically

**Typical Usage:**
- **Production releases**: Leave "Include debug APK" unchecked (default)
- **Testing releases**: Check "Include debug APK" to get both variants

## Requirements

The pipeline handles dependencies automatically, but requires GitHub Secrets configuration:
- Java 11 (Temurin distribution) - handled by workflow
- Android SDK API level 30 - handled by workflow  
- Gradle 6.7.1 - handled by workflow
- **GitHub Secrets for keystore** - must be configured manually

## Local Development

For local development, you'll need:
- Java 11 installed and configured
- Android SDK with API level 30
- Your own `app/keystore.properties` file with your keystore credentials

Build commands:
```bash
# Debug build (works with any keystore)
./gradlew clean assembleDebug

# Release build (requires proper keystore setup)
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Keystore Management

The build system expects keystore credentials but **never stores them in the repository**:
- **CI/CD**: Uses GitHub Secrets for secure credential management
- **Local development**: Requires manual `app/keystore.properties` setup
- **Repository**: No keystore files or credentials are committed