#!/bin/bash

# Test script to validate build pipeline components locally
# This script mimics parts of the GitHub Actions workflow for local testing

set -e

echo "🔧 Testing Build Pipeline Components"
echo "=================================="

# Check Java environment
echo "☕ Checking Java environment..."
export JAVA_HOME=/usr/lib/jvm/temurin-11-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
java -version

# Check if keystore.properties exists
echo "🔑 Checking keystore configuration..."
if [ -f "app/keystore.properties" ]; then
    echo "✅ keystore.properties found"
else
    echo "❌ keystore.properties missing"
    exit 1
fi

# Test changelog generation
echo "📝 Testing changelog generation..."
LAST_TAG=$(git tag --sort=-version:refname | head -n 1)
if [ -z "$LAST_TAG" ]; then
    echo "📅 No tags found, using full history"
    COMMIT_COUNT=$(git rev-list --count HEAD)
    echo "   Found $COMMIT_COUNT commits"
else
    echo "📅 Last tag: $LAST_TAG"
    COMMIT_COUNT=$(git rev-list --count "$LAST_TAG"..HEAD)
    echo "   Found $COMMIT_COUNT commits since last tag"
fi

# Test APK file discovery
echo "📱 Testing APK discovery..."
if [ -d "app/build/outputs/apk" ]; then
    DEBUG_APK=$(find app/build/outputs/apk/debug -name "*.apk" 2>/dev/null | head -n 1)
    RELEASE_APK=$(find app/build/outputs/apk/release -name "*.apk" 2>/dev/null | head -n 1)
    
    if [ -n "$DEBUG_APK" ]; then
        echo "✅ Debug APK found: $DEBUG_APK"
        echo "   Size: $(ls -lh "$DEBUG_APK" | awk '{print $5}')"
    else
        echo "ℹ️  No debug APK found (run './gradlew assembleDebug' first)"
    fi
    
    if [ -n "$RELEASE_APK" ]; then
        echo "✅ Release APK found: $RELEASE_APK"
        echo "   Size: $(ls -lh "$RELEASE_APK" | awk '{print $5}')"
    else
        echo "ℹ️  No release APK found (run './gradlew assembleRelease' first)"
    fi
else
    echo "ℹ️  No APK output directory found (run a build first)"
fi

# Test Gradle availability
echo "🔨 Testing Gradle..."
if [ -x "./gradlew" ]; then
    echo "✅ Gradle wrapper found and executable"
    echo "   Version: $(./gradlew --version | head -n 1)"
else
    echo "❌ Gradle wrapper not found or not executable"
    exit 1
fi

echo ""
echo "🎉 Build pipeline components validated successfully!"
echo ""
echo "To trigger the actual pipeline:"
echo "1. Go to GitHub Actions tab"
echo "2. Select 'Build and Release' workflow"  
echo "3. Click 'Run workflow'"
echo "4. Enter version tag (e.g., v1.5.1)"