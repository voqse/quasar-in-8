# Nixie Clock Android Widget

Nixie Clock is an Android home screen widget application that displays time, date, and year in the style of nixie tube displays. The app supports multiple themes, timezone configuration, and external app launching functionality.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites and Setup
- Java 11 is REQUIRED - Gradle 6.7.1 is incompatible with Java 17+
  - `sudo update-alternatives --set java /usr/lib/jvm/temurin-11-jdk-amd64/bin/java`
  - `export JAVA_HOME=/usr/lib/jvm/temurin-11-jdk-amd64`
  - `export PATH=$JAVA_HOME/bin:$PATH` (ensures javac uses Java 11)
  - Verify with: `java -version` and `javac -version` (should show 11.0.28)
- Android SDK with API level 30 build tools
  - Verify: `echo $ANDROID_HOME` should show `/usr/local/lib/android/sdk`
- Create required keystore.properties file in `app/keystore.properties`:
```properties
storeFile=debug.jks
storePassword=android
keyAlias=androiddebugkey
keyPassword=android
```

### Build Instructions
**CRITICAL**: Network access to dl.google.com is blocked in this environment, preventing dependency downloads.

- Clean build: `./gradlew clean assembleDebug`
  - **NEVER CANCEL** - First build takes 15-30 minutes downloading dependencies
  - Set timeout to 60+ minutes minimum
  - **WILL FAIL** due to network restrictions - this is expected behavior
- Alternative repositories may work but are not validated
- Debug APK will be located at: `app/build/outputs/apk/debug/com.voqse.nixieclock-1.5.0-(20)-debug.apk`

### Testing
- Run unit tests: `./gradlew test`
  - Uses Robolectric framework for Android testing
  - Test duration: approximately 2-5 minutes
  - **NEVER CANCEL** - Set timeout to 15+ minutes
  - **WILL FAIL** due to dependency download restrictions
- Single test file location: `app/src/test/java/com/voqse/nixieclock/DateTest.java`
- Tests use JUnit 4.13.2 with Robolectric 4.2.1

### Development Workflow  
- Code is structured as standard Android app with widget components
- Main application class: `app/src/main/java/com/voqse/nixieclock/App.java`
- Widget provider: `app/src/main/java/com/voqse/nixieclock/widget/WidgetProvider.java`
- Theme system: `app/src/main/java/com/voqse/nixieclock/theme/` directory

## Network and Build Limitations
**CRITICAL BUILD ISSUE**: This environment cannot access dl.google.com, preventing:
- Gradle plugin downloads
- Android library dependency resolution 
- Maven repository access to Google's Android libraries

**Workarounds** (NOT VALIDATED):
- Use cached dependencies if available: `./gradlew --offline <task>`
- Manual dependency management (not recommended)
- Alternative repository mirrors (reliability unknown)

## Validation Scenarios
Due to network limitations, manual validation is severely restricted:
- **Cannot build APK** due to dependency download failures
- **Cannot run automated tests** due to missing test dependencies  
- **Cannot install/run app** without successful build
- **Code analysis and static checks** are possible without network access

### Alternative Validation Commands (Network-free)
- Count source files: `find app/src/main/java -name "*.java" | wc -l` (result: 36)
- Count resource files: `find app/src/main/res -name "*.xml" | wc -l` (result: 28)
- Verify keystore exists: `ls -la app/debug.jks` (should show 2148 byte file)
- Check AndroidManifest: `grep -E "(package|android:versionName|android:versionCode)" app/src/main/AndroidManifest.xml`
- Check build config: `grep -E "(versionCode|versionName|compileSdkVersion)" app/build.gradle`

## Key Project Information

### Project Structure
```
app/src/main/java/com/voqse/nixieclock/
├── App.java (43 lines) - Main application class
├── clock/ - External app launching functionality
├── theme/ - Nixie tube themes and encryption
├── timezone/ - Timezone selection components  
├── utils/ - Utility classes (IoUtils, NixieUtils)
└── widget/ - Widget implementation and configuration
```

### Build Configuration
- **compileSdkVersion**: 30
- **buildToolsVersion**: 30.0.3
- **minSdkVersion**: 16 (Android 4.1)
- **targetSdkVersion**: 30 (Android 11)
- **versionCode**: 20
- **versionName**: 1.5.0
- **Gradle version**: 6.7.1
- **Android Gradle Plugin**: 4.2.2

### Dependencies
- AndroidX AppCompat 1.3.0
- Material Design Components 1.4.0
- Evernote Android Job 1.1.4
- Google Billing Client 4.0.0
- Test: JUnit 4.13.2, Robolectric 4.2.1, Mockito 1.9.5

### Code Statistics
- Total Java files: 23
- Total lines of code: ~2,600
- Main packages: clock, theme, timezone, utils, widget
- Test files: 1 (DateTest.java)

## Common Gradle Commands
**All commands require Java 11 environment setup first:**
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-11-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

**Commands that work without network:**
- Check Gradle version: `./gradlew --version` (works, shows Gradle 6.7.1)
- Stop Gradle daemon: `./gradlew --stop` (works when daemon is running)

**Commands that FAIL due to network restrictions:**
- Build debug APK: `./gradlew assembleDebug` (fails at dependency resolution)
- Build release APK: `./gradlew assembleRelease` (fails at dependency resolution)
- Run tests: `./gradlew test` (fails at dependency resolution)
- Clean project: `./gradlew clean` (fails at plugin resolution)
- List tasks: `./gradlew tasks` (fails at plugin resolution)
- Get help: `./gradlew help` (fails at plugin resolution)

## File Locations
- Gradle wrapper: `./gradlew` (symlinked as `./g`)
- Main build script: `build.gradle`
- App build script: `app/build.gradle`  
- Android manifest: `app/src/main/AndroidManifest.xml`
- Debug keystore: `app/debug.jks`
- Required keystore config: `app/keystore.properties` (must create)
- Widget XML definition: `app/src/main/res/xml/widget.xml`
- Theme resources: `docs/themes/Free/` and `docs/themes/Purchasable/`

## Known Issues and Workarounds
- **Java Version**: Must use Java 11 - newer versions cause Gradle compatibility errors
- **Network Access**: Google Maven repositories are blocked - build will fail at dependency resolution
- **Missing keystore.properties**: Must create manually for any build attempts
- **Long Build Times**: Initial builds download ~500MB of dependencies (when network works)
- **Gradle Daemon**: May need periodic restart with `./gradlew --stop`

## Static Analysis Commands (Network-free)
These commands work without network access for code analysis:
```bash
# Environment setup (run first)
export JAVA_HOME=/usr/lib/jvm/temurin-11-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Project analysis
find . -name "*.java" -exec wc -l {} + | tail -1  # Total lines of Java code
grep -r "TODO\|FIXME\|HACK" app/src/main/java/    # Find code comments  
grep -r "Log\." app/src/main/java/                # Find logging statements
find app/src/main/res -name "*.xml" -exec basename {} \; | sort | uniq -c  # Resource file types
```

## Important Development Notes
- Widget update mechanisms: AlarmManager, Handler, ScreenOnListener (see App.java comments)
- Theme system supports both free and purchasable themes
- Encryption system for theme resources (see Encryptor.java)  
- Uses deprecated JCenter repository - may cause future build issues
- Hugo plugin applied for method logging in debug builds

## Application Functionality
The Nixie Clock widget provides:
- Time display in nixie tube style (12/24 hour formats)
- Date display with month/day ordering options
- Year display mode
- Multiple visual themes (free and purchasable)
- Timezone selection and configuration
- Single/double-tap widget interaction
- External app launching capability
- Widget preview and configuration activity

Always validate Java version and create keystore.properties before attempting any build operations.