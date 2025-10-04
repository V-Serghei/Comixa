# Comixa

A modern Android app built with Kotlin, featuring a Navigation Drawer with 6 main sections and MVVM architecture.

## Features

- **Material3 Dark Theme**: Modern, beautiful dark theme throughout the app
- **Navigation Drawer**: Side menu with 6 main sections, each containing 2 sub-sections
- **MVVM Architecture**: Clean separation of concerns with ViewModels for each section
- **Fragment-based Navigation**: Using Android Navigation Component
- **Latest SDK**: Built with Android SDK 34

## Project Structure

### Main Sections

The app includes 6 main sections accessible from the navigation drawer:

1. **Home**
   - Recent
   - Favorites

2. **Library**
   - My Collection
   - Reading List

3. **Discover**
   - Trending
   - New Releases

4. **Community**
   - Forums
   - Groups

5. **Profile**
   - My Profile
   - Statistics

6. **Settings**
   - Preferences
   - About

### Architecture

The app follows the MVVM (Model-View-ViewModel) pattern:

- **View**: Fragments for each sub-section
- **ViewModel**: Dedicated ViewModel for each fragment
- **MainActivity**: Hosts the DrawerLayout and NavController

### Technologies Used

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture Components**:
  - Navigation Component
  - LiveData
  - ViewModel
  - View Binding
- **Material3**: For modern UI components and theming

## Building the App

To build this app, you'll need:

1. Android Studio Arctic Fox or later
2. JDK 8 or higher
3. Android SDK 34

### Build Instructions

```bash
./gradlew assembleDebug
```

### Running the App

Open the project in Android Studio and run it on an emulator or physical device running Android 7.0 (API 24) or higher.

## File Structure

```
app/
├── src/main/
│   ├── java/com/comixa/app/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── home/section1/HomeSection1Fragment.kt
│   │   │   ├── home/section2/HomeSection2Fragment.kt
│   │   │   ├── library/section1/LibrarySection1Fragment.kt
│   │   │   ├── library/section2/LibrarySection2Fragment.kt
│   │   │   ├── discover/section1/DiscoverSection1Fragment.kt
│   │   │   ├── discover/section2/DiscoverSection2Fragment.kt
│   │   │   ├── community/section1/CommunitySection1Fragment.kt
│   │   │   ├── community/section2/CommunitySection2Fragment.kt
│   │   │   ├── profile/section1/ProfileSection1Fragment.kt
│   │   │   ├── profile/section2/ProfileSection2Fragment.kt
│   │   │   ├── settings/section1/SettingsSection1Fragment.kt
│   │   │   └── settings/section2/SettingsSection2Fragment.kt
│   │   └── viewmodel/
│   │       ├── BaseViewModel.kt
│   │       └── [12 ViewModels for each fragment]
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── nav_header.xml
│   │   │   └── fragment_generic.xml
│   │   ├── menu/
│   │   │   └── drawer_menu.xml
│   │   ├── navigation/
│   │   │   └── nav_graph.xml
│   │   └── values/
│   │       ├── colors.xml (Material3 dark theme colors)
│   │       ├── strings.xml
│   │       └── themes.xml
│   └── AndroidManifest.xml
```

## License

This project is open source and available under the MIT License.