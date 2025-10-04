# Build Instructions for Comixa

## Prerequisites

1. **Android Studio**: Arctic Fox (2020.3.1) or newer
   - Download from: https://developer.android.com/studio

2. **JDK**: Java Development Kit 8 or higher
   - Recommended: JDK 11 or JDK 17
   - Android Studio includes a JDK, but you can use your own

3. **Android SDK**:
   - SDK Platform 34 (Android 14)
   - SDK Build-Tools 34.0.0 or higher
   - Android SDK Platform-Tools
   - These will be automatically installed by Android Studio

## Setup Steps

### Option 1: Using Android Studio (Recommended)

1. **Clone the repository**:
   ```bash
   git clone https://github.com/V-Serghei/Comixa.git
   cd Comixa
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the Comixa directory and click "OK"

3. **Gradle Sync**:
   - Android Studio will automatically start syncing Gradle
   - Wait for the sync to complete (first time may take a few minutes)
   - If prompted, accept any SDK installation requests

4. **Build the project**:
   - Click on "Build" → "Make Project" (Ctrl+F9 / Cmd+F9)
   - Wait for the build to complete

5. **Run the app**:
   - Connect an Android device via USB or start an emulator
   - Click the "Run" button (green play icon) or press Shift+F10
   - Select your device and click "OK"

### Option 2: Using Command Line

1. **Clone the repository**:
   ```bash
   git clone https://github.com/V-Serghei/Comixa.git
   cd Comixa
   ```

2. **Set ANDROID_HOME environment variable**:
   ```bash
   # Linux/Mac
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/platform-tools

   # Windows (PowerShell)
   $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
   $env:PATH += ";$env:ANDROID_HOME\platform-tools"
   ```

3. **Build the project**:
   ```bash
   # Linux/Mac
   ./gradlew assembleDebug

   # Windows
   gradlew.bat assembleDebug
   ```

4. **Install on device** (device must be connected via USB with USB debugging enabled):
   ```bash
   # Linux/Mac
   ./gradlew installDebug

   # Windows
   gradlew.bat installDebug
   ```

## Common Build Tasks

### Clean Build
```bash
./gradlew clean assembleDebug
```

### Build Release APK
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

### Check for Lint Issues
```bash
./gradlew lint
```

## Troubleshooting

### Build fails with "SDK not found"
- Make sure ANDROID_HOME is set correctly
- Open Android Studio and let it download the SDK
- Verify SDK location in Android Studio: File → Settings → Appearance & Behavior → System Settings → Android SDK

### Gradle sync fails
- Check your internet connection (Gradle needs to download dependencies)
- Try: File → Invalidate Caches / Restart
- Delete `.gradle` folder and sync again

### "Could not resolve dependencies" error
- Make sure you have internet access to:
  - https://dl.google.com (Google Maven repository)
  - https://repo.maven.apache.org (Maven Central)
- Check if you're behind a proxy and configure Gradle accordingly

### ViewBinding not working
- Make sure you have:
  ```kotlin
  buildFeatures {
      viewBinding = true
  }
  ```
  in your `app/build.gradle.kts` file
- Clean and rebuild the project

## Device Requirements

- **Minimum**: Android 7.0 (API 24)
- **Recommended**: Android 10 (API 29) or higher
- **RAM**: 1 GB minimum, 2 GB recommended

## First Run

When you first run the app, you'll see:
1. A toolbar at the top with the app name "Comixa"
2. A menu icon (hamburger menu) in the top left
3. Click the menu icon to open the navigation drawer
4. Navigate through the 6 main sections and their sub-sections

## Development Tips

1. **Enable instant run** for faster development iterations
2. **Use an emulator** with hardware acceleration (HAXM on Intel, WHPX on Windows)
3. **Keep SDK and tools updated** via Android Studio's SDK Manager
4. **Use Logcat** to debug: View → Tool Windows → Logcat

## Next Steps After Building

1. Explore the code structure in ARCHITECTURE.md
2. Check PROJECT_SUMMARY.md for an overview
3. Start adding features to the fragments
4. Customize the theme colors in `res/values/colors.xml`
5. Add more UI components and functionality

## Support

If you encounter issues:
1. Check the logs in Android Studio's "Build" window
2. Look at Logcat for runtime errors
3. Verify all prerequisites are met
4. Try cleaning and rebuilding the project

Happy coding! 🚀
