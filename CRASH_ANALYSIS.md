# Crash Analysis Report — Day 2 Controlled Crash Test

**Project Name:** Employee Task Manager

**Project Path:** `C:\Users\HP\AndroidStudioProjects\Employeetaskmanager`

**Author:** Android Developer Bootcamp Student / Senior Android Debugging Engineer

**Date:** Day 2 Bootcamp Evaluation

---

## 1. Crash Overview

During the Day 2 debugging exercise, a controlled and intentional runtime exception was introduced into [MainActivity.kt](app/src/main/java/com/example/employeetaskmanager/MainActivity.kt) to examine Logcat crash reporting, stack trace reading, exception handling, and recovery workflows. This was an educational debugging drill and **not** an original application defect.

---

## 2. Device and Application Information

* **Application ID / Package Name:** `com.example.employeetaskmanager`
* **Target Device:** Xiaomi Redmi Note 8 Pro (Physical Android Device)
* **Timestamp:** `2026-11-17 12:05:38.643`
* **Process ID (PID):** `24164`
* **Build Variant:** Debug (`assembleDebug`)
* **Operating System:** Android (API Level 24+)

---

## 3. Reproduction Steps

To replicate the controlled crash scenario for educational debugging practice:
1. Open [MainActivity.kt](app/src/main/java/com/example/employeetaskmanager/MainActivity.kt).
2. Insert a deliberate exception invocation immediately following `setContentView(R.layout.layout)` inside `onCreate()`:
   ```kotlin
   throw IllegalStateException("Day 2 controlled crash test")
   ```
3. Rebuild and run the application on the target physical device (Xiaomi Redmi Note 8 Pro).
4. Observe the immediate application termination and the corresponding fatal exception dump in Android Studio Logcat.

---

## 4. Expected Behavior

Under normal operating conditions:
* The application initializes `MainActivity`.
* `super.onCreate()` restores activity state.
* `setContentView(R.layout.layout)` inflates the user interface XML layout.
* The application launches smoothly and displays the welcome screen without throwing unhandled exceptions.

---

## 5. Actual Behavior

Upon launching the application with the intentional exception injected:
* The Android OS invoked `MainActivity.onCreate()`.
* Immediately after inflating `R.layout.layout`, the `IllegalStateException` was triggered.
* Because the exception was unhandled on the main (UI) thread, the Android runtime forcefully terminated the process.
* The application crashed instantly upon launch, logging a fatal exception stack trace in Logcat.

---

## 6. Logcat Exception Analysis

The exact Logcat output captured during the runtime crash:

```text
E/AndroidRuntime(24164): FATAL EXCEPTION: main
Process: com.example.employeetaskmanager, PID: 24164
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.employeetaskmanager/com.example.employeetaskmanager.MainActivity}: java.lang.IllegalStateException: Day 2 controlled crash test
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3576)
at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3748)
at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:85)
at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:135)
at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2187)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loop(Looper.java:236)
at android.app.ActivityThread.main(ActivityThread.java:8057)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:620)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1011)
Caused by: java.lang.IllegalStateException: Day 2 controlled crash test
at com.example.employeetaskmanager.MainActivity.onCreate(MainActivity.kt:12)
at android.app.Activity.performCreate(Activity.java:8142)
at android.app.Activity.performCreate(Activity.java:8114)
at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1309)
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3549)
... 11 more
```

---

## 7. Stack Trace Explanation

1. **`FATAL EXCEPTION: main`**: Indicates that an unhandled exception occurred on the application's main thread (UI thread), causing the Android Runtime (ART) to abort the process.
2. **`RuntimeException` vs `IllegalStateException`**:
   * **`IllegalStateException`**: The underlying root exception thrown directly by application code (`MainActivity.kt:12`) signaling that the application was invoked in an inappropriate state.
   * **`RuntimeException`**: A wrapper exception thrown by the Android framework (`ActivityThread`) when an activity lifecycle method (`performLaunchActivity`) fails during startup.
3. **`at com.example.employeetaskmanager.MainActivity.onCreate(MainActivity.kt:12)`**: Points to the exact file (`MainActivity.kt`) and line number (`Line 12`) where the root exception originated.

---

## 8. Root Cause

The crash was caused by an explicit, intentional `IllegalStateException` statement placed inside `MainActivity.onCreate()` at line 12. Because no `try-catch` block enclosed the exception, it propagated up to the Android framework's main thread handler, resulting in a fatal process termination.

---

## 9. Resolution and Fix

To restore the application to a stable state:
1. The intentional throw statement was removed by restoring the original `MainActivity.kt` from its backup.
2. SHA-256 file hash verification confirmed the restored file matched the backup.
3. Gradle command `:app:assembleDebug` completed successfully.
4. The app launched successfully on the connected Xiaomi Redmi Note 8 Pro.

---

## 10. Post-Fix Verification

* **Build Status:** Gradle `:app:assembleDebug` completed successfully.
* **Deployment:** The application was successfully deployed and launched on the connected Xiaomi Redmi Note 8 Pro device.
* **Logcat Verification:** Post-restoration Logcat verification showed no new application crash.

---

## 11. Lessons Learned

* **Logcat Proficiency:** Learned how to isolate `FATAL EXCEPTION: main` and trace root causes using file and line number references (`MainActivity.kt:12`).
* **Main Thread Safety:** Understood that unhandled exceptions on the UI thread immediately crash the Android application process.
* **Backup & Recovery Protocol:** Verified the importance of maintaining pre-change file backups during debugging and experimentation drills.

---

## 12. Evidence Screenshot References

The following evidence screenshots are captured and stored in the dedicated project screenshot directory:

1. **Application Crash UI:** `docs/week 1/day 2/screenshot/application crash.png`
2. **Logcat Exception Output:** `docs/week 1/day 2/screenshot/logcat.png`

*Note:* No sensitive information or credentials are exposed in build outputs or device logs.
