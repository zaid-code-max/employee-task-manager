# Setup & Troubleshooting Issues

This document records all common setup issues, environment errors, and troubleshooting steps encountered during the development and setup of the **Employee Task Manager** project.

---

## ⚙️ Documented Setup Issues & Resolutions

### 1. `git : The term 'git' is not recognized as the name of a cmdlet`

* **Symptom:**
  Running `git --version` or any `git` command in PowerShell returns:
  ```text
  git : The term 'git' is not recognized as the name of a cmdlet, function, script file, or operable program.
  ```
* **Cause:**
  Git is installed at `C:\Program Files\Git\cmd\git.exe`, but `C:\Program Files\Git\cmd` was missing from the User `PATH` environment variable or the active PowerShell session.
* **Resolution:**
  * **Immediate Fix (Current Session):** Run the following command in PowerShell:
    ```powershell
    $env:Path += ";C:\Program Files\Git\cmd"
    ```
  * **Permanent Fix:** Add `C:\Program Files\Git\cmd` to the User Environment Variables under `PATH` and restart Android Studio / Terminal.

---

### 2. PowerShell Terminal Environment Variables Not Refreshing

* **Symptom:**
  Adding or modifying an environment variable in Windows (or via PowerShell script) does not update existing open terminal tabs in Android Studio.
* **Cause:**
  PowerShell processes load a static snapshot of environment variables at startup and do not dynamically reload PATH changes from registry.
* **Resolution:**
  * Refresh `$env:Path` in the current terminal tab:
    ```powershell
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","User") + ";" + [System.Environment]::GetEnvironmentVariable("Path","Machine")
    ```
  * Or close the current terminal tab and open a new terminal tab in Android Studio.

---

### 3. Redeclaration Error During Java to Kotlin Migration

* **Symptom:**
  IDE reports `Error: Redeclaration: class MainActivity` during conversion from Java to Kotlin.
* **Cause:**
  Both `MainActivity.java` and `MainActivity.kt` existed simultaneously in `com.example.employeetaskmanager`.
* **Resolution:**
  Delete `MainActivity.java` after creating `MainActivity.kt`, then run `./gradlew assembleDebug` to refresh build artifacts.

---

### 4. SDK Version Mismatch (Compile SDK 37)

* **Symptom:**
  Gradle build fails with an error indicating missing SDK platform for API level 37.
* **Cause:**
  The project compiles against Android SDK 37 (`compileSdk = 37`), which may not be pre-installed on every developer machine.
* **Resolution:**
  1. Open Android Studio **SDK Manager** (`Tools` -> `SDK Manager`).
  2. Under **SDK Platforms**, check **Android 16.0 (Baklava) / API Level 37**.
  3. Click **Apply** to download and install.
  4. Sync Gradle in Android Studio (`File` -> `Sync Project with Gradle Files`).
