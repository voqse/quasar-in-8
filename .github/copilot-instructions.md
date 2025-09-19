# Quasar-in-8 Android Nixie Clock Widget

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Project Overview
This is an Android application that provides customizable Nixie Clock home screen widgets. The app includes multiple theme variants (old, new, neo, tron) with different visual styles and supports features like timezone selection, date format customization, and external clock app launching.

## CRITICAL BUILD REQUIREMENTS

### Java Version Compatibility
- **ALWAYS use Java 11** - Java 17 is incompatible with Gradle 6.7.1
- **NEVER use Java 17** - it will cause "Unsupported class file major version 61" errors
- Set Java environment:
  ```bash
  export JAVA_HOME=/usr/lib/jvm/temurin-11-jdk-amd64
  export PATH=$JAVA_HOME/bin:$PATH
  ```
- Verify Java version before any Gradle commands: `java -version`

### Network Dependencies Issue
- **BUILD FAILS due to network restrictions** - Google Maven repositories (dl.google.com, maven.google.com) are blocked
- **No offline cached dependencies available** - fresh environment cannot build
- **WORKAROUND: Document commands that would work in network-enabled environment**
- Expected build commands (NETWORK REQUIRED):
  ```bash
  ./gradlew clean       # Clean build - takes ~2 minutes
  ./gradlew build       # Full build - ESTIMATED 15-20 minutes. NEVER CANCEL. Set timeout to 40+ minutes.
  ./gradlew test        # Run unit tests - takes ~5 minutes. NEVER CANCEL. Set timeout to 15+ minutes.
  ```

## Working Effectively

### Build System
- **Android Gradle Plugin 4.2.2** with **Gradle 6.7.1**
- **Target SDK: 30** (Android 11)
- **Min SDK: 16** (Android 4.1)
- **Build tools: 30.0.3**

### Environment Setup (NETWORK REQUIRED)
1. Install Java 11 (NOT Java 17)
2. Set JAVA_HOME and PATH
3. Ensure network access to Google Maven repositories
4. Run: `./gradlew --version` to verify setup

### Build Commands (NETWORK REQUIRED)
- `./gradlew clean` - Clean previous build artifacts - takes ~2 minutes
- `./gradlew build` - Full build including all variants - ESTIMATED 15-20 minutes. NEVER CANCEL. Set timeout to 40+ minutes.
- `./gradlew assembleDebug` - Build debug APK - ESTIMATED 10-15 minutes. NEVER CANCEL. Set timeout to 30+ minutes.
- `./gradlew assembleRelease` - Build release APK (requires keystore.properties) - ESTIMATED 15-20 minutes. NEVER CANCEL. Set timeout to 40+ minutes.

### Testing (NETWORK REQUIRED)
- `./gradlew test` - Run unit tests using Robolectric - takes ~5 minutes. NEVER CANCEL. Set timeout to 15+ minutes.
- **Single test file:** `app/src/test/java/com/voqse/nixieclock/DateTest.java`
- **Test framework:** JUnit 4 + Robolectric 4.2.1 (for Android SDK 23)
- **Test validates:** Date formatting in 24h/12h modes across different timezones

### Project Structure
```
app/src/main/java/com/voqse/nixieclock/
├── App.java                    # Application entry point
├── clock/                      # External app launching
│   ├── ExternalApp.java        # Manages third-party app integration  
│   └── ClockApp.java           # Clock app utilities
├── theme/                      # Visual themes and resources
│   ├── Theme.java              # Theme definitions
│   ├── ThemeResources.java     # Resource loading (encrypted)
│   ├── ResourceCipher.java     # Asset encryption/decryption
│   ├── Encryptor.java          # Standalone encryption utility (NOT in APK)
│   └── drawer/                 # Theme-specific rendering
├── timezone/                   # Timezone handling
├── utils/                      # Utility classes
│   ├── NixieUtils.java         # Device utilities
│   └── IoUtils.java            # File I/O operations
└── widget/                     # Widget implementation
    ├── WidgetProvider.java     # Main widget provider
    ├── ConfigurationActivity.java # Widget setup UI
    ├── Settings.java           # Preferences management
    └── support/                # UI components
```

### Key Features
- **Home Screen Widget:** Displays time in Nixie tube style
- **Multiple Themes:** 4 visual variants (old, new, neo, tron) with assets in `app/src/main/assets/themes/`
- **Timezone Support:** Custom timezone selection beyond system default
- **Encrypted Assets:** Theme resources are encrypted using ResourceCipher
- **Billing Integration:** Google Play Billing API 4.0.0 for premium themes
- **External App Launch:** Can launch other clock applications on widget click

### Important Files
- `app/src/main/AndroidManifest.xml` - App configuration and widget definitions
- `app/build.gradle` - Build configuration and dependencies
- `app/src/main/res/xml/widget.xml` - Widget definition
- `app/proguard-rules.pro` - Code obfuscation rules
- `docs/themes/` - Theme categorization (Free vs Purchasable)

## Validation (REQUIRES NETWORK ACCESS)

### Manual Testing Scenarios
After building the application:
1. **Install APK:** `adb install app/build/outputs/apk/debug/app-debug.apk`
2. **Add Widget:** Long press home screen → Widgets → Add Nixie Clock widget  
3. **Test Configuration:** Tap widget → Should open configuration activity
4. **Theme Selection:** Verify theme switching works correctly
5. **Timezone Test:** Change timezone and verify time display updates
6. **External App:** Configure external clock app and test launch

### Code Quality
- **No linting configuration** - basic Android lint only
- **Test Coverage:** Limited - only DateTest.java exists
- **Always run:** `./gradlew build` before submitting changes to ensure APK builds correctly

## Common Issues

### Build Failures
- **Java version error:** Switch to Java 11
- **Network timeout:** Increase Gradle daemon timeout in gradle.properties
- **Missing keystore:** Release builds require `app/keystore.properties` file

### Development Workflow  
- **Theme Changes:** Modify assets in `app/src/main/assets/themes/`
- **Widget Logic:** Main implementation in `WidgetProvider.java`
- **UI Changes:** Configuration activity layouts in `app/src/main/res/layout/`
- **Testing:** Run DateTest.java to verify time formatting logic

## Dependencies
- **AndroidX:** Modern Android support libraries
- **Material Design:** Google Material Components 1.4.0
- **Evernote Job:** Background task scheduling 1.1.4  
- **Google Play Billing:** In-app purchases 4.0.0
- **Test Dependencies:** JUnit 4, Robolectric 4.2.1, Mockito

## Build Artifacts
- **Debug APK:** `app/build/outputs/apk/debug/`
- **Release APK:** `app/build/outputs/apk/release/` (requires signing)
- **Generated classes:** `app/build/generated/`

Remember: This project requires network access to Google Maven repositories for builds. All build and test commands will fail in network-restricted environments.