# 📸 Gallery Lite

> A fast, modern, and privacy-first Android photo & video gallery built with **Jetpack Compose**, **Material 3**, and **Kotlin Coroutines**.

![Reimagined Gallery Lite UI](https://raw.githubusercontent.com/placeholder/preview.jpg)

---

## ✨ Features

- **🧱 Dynamic Masonry Grid**: Photos flow in an organic, Pinterest-style staggered layout preserving natural aspect ratios.
- **✨ Spotlight Hero Card**: Your latest capture receives an editorial full-width showcase with smart time & date captions.
- **⏳ "On This Day" Time Capsule**: Dedicated flashback tab showing pictures captured on this exact calendar day 1, 2, 3+ years ago.
- **🪟 Floating Frosted Glass Navigation**: Modern glassmorphic navigation bar with active tab glow indicator.
- **📷 Full-Screen Pinch-to-Zoom Viewer**: Smooth gestures (1x–5x zoom, double tap, drag/pan), auto-hiding controls, and full EXIF metadata bottom sheet.
- **📂 Albums & Folders**: Automatic aggregation of camera, screenshots, WhatsApp, and download folders with count badges.
- **⭐ Local Favorites**: Instant hearting backed by Room database persistence.
- **🔒 Privacy Guaranteed**: **0 internet permissions** in `AndroidManifest.xml` — zero analytics, zero cloud syncing, 100% offline.
- **⚡ Super Lite**: Engineered for an APK size under 8 MB with R8 shrinking and baseline profiles.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.1+
- **UI Framework**: Jetpack Compose + Material 3 (with Dynamic Color)
- **Image & Video Loading**: Coil 3 with video frame decoding
- **Local Storage**: Android Room 2.6.1 + Scoped Storage `MediaStore`
- **Navigation**: Jetpack Compose Navigation
- **Architecture**: MVVM with unidirectional Flow streams

---

## 📂 Project Structure

```
gallery-lite/
├── gradle/
│   └── libs.versions.toml             # Central Version Catalog
├── build.gradle.kts                   # Root build script
├── settings.gradle.kts                # Project & repository settings
├── gradle.properties                  # Memory and compiler options
└── app/
    ├── build.gradle.kts               # App module build configuration
    ├── proguard-rules.pro             # R8 optimization & shrinking rules
    └── src/main/
        ├── AndroidManifest.xml        # Scoped permissions (no internet)
        ├── res/                       # Strings, theme, and extraction rules
        └── java/com/gallerylite/
            ├── GalleryApp.kt          # Coil 3 & video decoder setup
            ├── MainActivity.kt        # Edge-to-edge entrypoint & permissions
            ├── data/
            │   ├── model/             # MediaItem, Album, OnThisDayMemory
            │   ├── local/             # Room DB, DAOs, and Entities
            │   └── repository/        # MediaStore queries & memory grouping
            ├── ui/
            │   ├── theme/             # Midnight palette, M3 type & shapes
            │   ├── navigation/        # Routes & NavHost
            │   ├── components/        # FloatingNavBar, MasonryFeed, HeroCard
            │   └── screens/           # Photos, OnThisDay, Albums, Viewer
            └── util/                  # DateUtils, FileUtils, PermissionUtils
```

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17
- Android SDK 35 (Android 15)

### Steps
1. Open Android Studio.
2. Select **Open** and choose the `gallery-lite` folder.
3. Allow Gradle to sync dependencies from the version catalog.
4. Run the app on an Android device or emulator running **Android 10 (API 29)** or newer.
