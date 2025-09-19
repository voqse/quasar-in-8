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

## Required GitHub Secrets

Before using the pipeline, configure these secrets in your repository settings:

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
2. **Setup Keystore**: Create keystore configuration from GitHub Secrets
3. **Generate Changelog**: Collect all commit messages since the last release
4. **Build APKs**: Create both debug and release versions
5. **Create Release**: Generate a GitHub release with:
   - Version tag you specified
   - Changelog with commit history
   - Debug APK attachment (~19.5 MB)
   - Release APK attachment (~17.8 MB, optimized)
   - Build artifacts (retained for 30 days)

## APK Outputs

- **Debug APK**: `nixieclock-{version}-debug.apk` - includes debugging symbols, suitable for testing
- **Release APK**: `nixieclock-{version}-release.apk` - optimized and minified for distribution

## Security Approach

The pipeline uses **GitHub Secrets** to securely store keystore credentials:
- Keystore file is base64-encoded and stored as a secret
- Credentials are injected during build time only
- No sensitive information is stored in the repository
- Both debug and release builds are supported securely

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