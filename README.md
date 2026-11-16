# Employee Task Manager

An Android application designed for managing employee tasks and workflow tracking.

---

## Project Overview

* **Application Name:** Employee Task Manager
* **Package Name:** `com.example.employeetaskmanager`
* **Programming Language:** Kotlin
* **Minimum SDK:** API 24 (Android 7.0 Nougat)
* **Target / Compile SDK:** API 37
* **Build System:** Gradle (Kotlin DSL `.gradle.kts`) with Version Catalog (`libs.versions.toml`)
* **AGP Version:** 9.5.1

---

## Project Structure

```text
Employeetaskmanager/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/employeetaskmanager/
│   │   │   │   └── MainActivity.kt          # Main Activity (Kotlin)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── layout.xml           # Main screen layout
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                           # Unit tests
│   │   └── androidTest/                    # Instrumentation tests
│   └── build.gradle.kts                     # App-level build script
├── gradle/
│   └── libs.versions.toml                   # Gradle Version Catalog
├── build.gradle.kts                         # Root-level build script
└── settings.gradle.kts                      # Gradle settings
```

---

## Tech Stack & Dependencies

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** Android XML Views with Material Design Components
* **Core Libraries:**
  * `androidx.core:core-ktx`: Kotlin extensions for Android core APIs
  * `androidx.appcompat:appcompat`: Backward compatibility for UI components
  * `com.google.android.material:material`: Material Design UI components
* **Testing:**
  * JUnit 4 (`junit:junit`)
  * AndroidX Test JUnit (`androidx.test.ext:junit`)
  * Espresso (`androidx.test.espresso:espresso-core`)

---

## How to Build and Run

### Prerequisites
* **Android Studio:** Ladybug or newer recommended
* **JDK:** Java 11 or higher
* **Android SDK:** API 37 installed

### Command Line Build

1. **Clone or navigate to the project directory:**
   ```powershell
   cd Employeetaskmanager
   ```

2. **Build Debug APK:**
   ```powershell
   .\gradlew.bat assembleDebug
   ```

3. **Run Unit Tests:**
   ```powershell
   .\gradlew.bat test
   ```

4. **Install on Connected Device / Emulator:**
   ```powershell
   .\gradlew.bat installDebug
   ```

---

## Troubleshooting & Setup Issues

For a detailed list of setup issues, PowerShell environment configurations, Git fixes, and troubleshooting steps, see [SETUP_ISSUES.md](./SETUP_ISSUES.md).

---

## License

This project is configured as a standard Android application.
