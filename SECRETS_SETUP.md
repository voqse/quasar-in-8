# GitHub Secrets Setup Guide

This document explains how to configure GitHub Secrets for automated **release APK** builds. Debug APKs use temporary keystores generated during builds and don't require secrets.

## Required Secrets

The build pipeline requires these four secrets for release APK generation:

### 1. KEYSTORE_PASSWORD
- **Description**: Password for your release keystore file
- **Example**: `myKeystorePassword123`
- **Source**: The password you used when creating your keystore

### 2. KEY_ALIAS  
- **Description**: The alias of your signing key within the keystore
- **Example**: `myapp-release-key`
- **Source**: The key alias you specified when creating your signing key

### 3. KEY_PASSWORD
- **Description**: Password for your specific signing key (may be same as keystore password)
- **Example**: `myKeyPassword456`
- **Source**: The password you set for your signing key

### 4. RELEASE_KEYSTORE
- **Description**: Base64-encoded content of your release keystore file (.jks)
- **How to create**: 
  ```bash
  # Encode your keystore file to base64 (single line, no wrapping)
  base64 -w 0 path/to/your-keystore.jks
  ```
- **Result**: A long base64 string like `MIIE...` (copy the entire output)

## Creating a Release Keystore

If you don't have a release keystore yet, create one:

```bash
# Generate a new keystore
keytool -genkey -v -keystore nixie-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias nixie-key

# You'll be prompted for:
# - Keystore password (save this for KEYSTORE_PASSWORD secret)
# - Key password (save this for KEY_PASSWORD secret) 
# - Your information (name, organization, etc.)
```

## Setting Up Secrets in GitHub

1. Go to your repository on GitHub
2. Click **Settings** tab
3. Click **Secrets and variables** → **Actions**
4. Click **New repository secret**
5. Add each secret:
   - Name: `KEYSTORE_PASSWORD`, Value: your keystore password
   - Name: `KEY_ALIAS`, Value: your key alias (e.g., `nixie-key`)
   - Name: `KEY_PASSWORD`, Value: your key password  
   - Name: `RELEASE_KEYSTORE`, Value: base64-encoded keystore content

## Verification

After setting up secrets, the build pipeline will:
- ✅ Create keystore.properties from secrets for release builds
- ✅ Decode and create release keystore file
- ✅ Build release APK (always)
- ✅ Optionally build debug APK with temporary keystore (if requested)
- ✅ Attach APKs to GitHub releases

## Security Benefits

- 🔒 No keystore files in repository
- 🔒 No passwords in code or configs  
- 🔒 Secrets only used for release builds
- 🔒 Debug builds use temporary keystores (no secrets needed)
- 🔒 Full audit trail of secret usage
- 🔒 Easy rotation of credentials if needed

## Troubleshooting

**Build fails with "keystore not found"**
- Check that RELEASE_KEYSTORE secret contains valid base64 data
- Verify base64 encoding was done with `-w 0` flag (no line wrapping)

**Build fails with "incorrect password"**
- Verify KEYSTORE_PASSWORD matches your keystore password
- Verify KEY_PASSWORD matches your key password
- Verify KEY_ALIAS matches exactly (case-sensitive)

**APK signing verification fails**
- Ensure the keystore was created properly
- Check that all credentials match the keystore
- Verify keystore validity period hasn't expired