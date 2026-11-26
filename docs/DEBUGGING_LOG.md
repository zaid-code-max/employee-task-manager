# Employee Task Manager - Debugging Log

This document records observed issues, investigation results, fixes, and verification. Unverified root causes or tests are marked as pending rather than assumed.

For environment setup and general troubleshooting, refer to `SETUP_ISSUES.md`.

---

## 1. KSP Generated Source Registration Error

**Date:** 17 November 2026

**Category:** Gradle / Kotlin Symbol Processing

### Symptom
Gradle configuration failed after adding Room and KSP dependencies.

The build reported:

`Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin.`

### Cause
The initial KSP plugin version was `2.2.10-2.0.2`. In the configured AGP 9 built-in Kotlin environment, this setup triggered an attempt to register generated Kotlin source directories through the `kotlin.sourceSets` DSL.

### Resolution
Updated the KSP version in `gradle/libs.versions.toml` to `2.3.10`. No `android.disallowKotlinSourceSets=false` workaround was added.

### Verification
- Gradle Sync: Successful.
- Gradle Build: Successful.
- KSP code generation: Verified.

---

## 2. TaskViewModel Direct Storage Dependency

**Date:** 17 November 2026

**Category:** Architecture Review

### Finding
The initial `TaskViewModel` implementation directly created `AppDatabase` and accessed `TaskDao`, violating the approved architecture boundary:

`Screen -> ViewModel -> Repository -> Storage`

### Resolution
- Changed `TaskViewModel` to extend `ViewModel`.
- Injected `TaskRepository` through the constructor.
- Created `TaskViewModelFactory`.
- Moved database, DAO, and Repository construction into the factory.
- Updated `TaskDetailsActivity` to obtain the ViewModel through the factory.

### Verification
- ViewModel and Activity boundary inspection: Passed.
- Gradle Build: Successful.
- Unit Tests: 1 passed, 0 skipped, 0 failed.

---

## 3. Day 4 Controlled Bug Challenges

**Scope:** Task List screen only, as required by the Day 4 slide.

### Bug A — Button tap does nothing

**Reported behavior:** A button tap does not trigger its intended action.

**Inspection:** The Home navigation button has a click listener in `MainActivity`. In `activity_task_details.xml`, the empty-state view with ID `textViewEmptyState` is a `LinearLayout`, but the Activity previously requested it as a `TextView`.

**Source-level root cause:** The invalid `TextView` cast can throw a `ClassCastException` during `TaskDetailsActivity.onCreate()`. This can make the Home button appear unresponsive because navigation starts the Activity but the destination crashes while being created.

**Fix applied:** Changed the lookup to `findViewById<View>(R.id.textViewEmptyState)`, matching the actual container type. The Home navigation click listener itself was preserved.

**Retest:** A runtime stack trace / breakpoint confirmation and device retest after the scope cleanup are still pending.

### Bug B — Task list fails to display or update correctly

**Reported behavior:** Task records do not display or refresh correctly.

**Code change retained:** `TaskViewModel` now uses `SharingStarted.Eagerly` for its Room-backed task Flow so the ViewModel starts collecting task updates for its lifetime.

**Current finding:** Earlier device notes reported that a newly added task appeared immediately. However, the original failure trace and the exact root cause were not captured.

**Root cause:** Not yet confirmed.

**Fix / retest:** The Flow collection change is retained as the current correction attempt. Reproduce the original issue, inspect the emitted task list and adapter submission with breakpoints, and verify add/completion updates after this scope reduction.

### Post-cleanup verification

**Date:** 20 November 2026

- Clean Debug APK build (`.\gradlew.bat clean assembleDebug`): Successful.
- Unit tests (`.\gradlew.bat testDebugUnitTest`): Successful.
- Device installation (`.\gradlew.bat installDebug`): Successful.
- Connected device reported by Gradle: `CPH1877 - 10`.
- User confirmed that the app is working after installation.

### Remaining debugging evidence

- Bug A: The source-level view-type mismatch was corrected. Breakpoint confirmation and a specific runtime reproduction/retest are not yet documented.
- Bug B: The original root cause is not confirmed. `SharingStarted.Eagerly` remains a correction attempt; inspect the emitted task list and adapter submission with breakpoints.
- Terminal screenshots of the post-cleanup build, unit-test, and installation results still need to be captured.

---

## Week 2 Day 8 — Bug 1: Malformed JSON / Missing Required Field

**Date:** 26 November 2026

### Symptom

Malformed JSON or a missing required API response field can cause JSON parsing to fail.

### Reproduction

Added two deterministic unit-test scenarios in `TaskApiClientTest.kt`:

1. `fetchTasks_malformedJson_returnsFailureAndDisconnects`
   - Supplied an incomplete JSON array.
2. `fetchTasks_missingRequiredField_returnsFailureAndDisconnects`
   - Supplied a task without the required `completed` field.

### Observations

- Malformed JSON is rejected during `JSONArray(responseBody)` parsing at `TaskApiClient.kt:72`.
- Missing required fields are rejected by JSON accessors, including `item.getBoolean("completed")` at line 88.
- `fetchTasks()` catches parsing exceptions and returns `Result.failure`.
- The connection is disconnected in the `finally` block at line 62.
- `TaskViewModel` maps failure to `ApiTaskUiState.Error`.
- `TaskDetailsActivity` displays the error state and Retry action.

### Root Cause

Malformed JSON and incomplete task objects cause JSON parsing exceptions because the expected response structure or required fields are unavailable.

### Resolution

- No production-code modification was required.
- Added regression tests for malformed JSON and missing required fields.
- Existing exception handling and connection cleanup were retained.

### Verification Evidence

- Targeted malformed JSON test: PASS.
- Targeted missing required field test: PASS.
- Gradle result: BUILD SUCCESSFUL.
- XML test report: `app/build/test-results/testDebugUnitTest/TEST-com.example.employeetaskmanager.TaskApiClientTest.xml`.
- Both test cases were independently identified as passing in the XML report.

### Verification Limitation

The parser failures were reproduced through deterministic unit tests. An actual unhandled application crash on a physical device was not established. The ViewModel and Activity error-handling path was inspected in source; an end-to-end malformed-response device test was not performed.

### Final Result

**Parser failure handling verified. No confirmed unhandled crash. Regression tests added.**

---

## Week 2 Day 8 — Bug 2: HTTP 500 / Network Timeout

**Date:** 26 November 2026

### Reproduction

1. HTTP 500 response simulated using FakeHttpURLConnection.
2. SocketTimeoutException simulated during response-code retrieval.

### Observations

- HTTP 500 is rejected by the non-2xx response check in TaskApiClient.kt.
- The client returns ApiHttpException with status code 500.
- SocketTimeoutException is caught by the existing exception handler.
- Both failure paths execute the finally block and disconnect the connection.

### Root Cause

- HTTP 500 represents an unsuccessful server response.
- A network timeout prevents the client from receiving a response within the configured timeout.

### Resolution

- No production-code modification was required.
- Added a dedicated HTTP 500 regression test.
- Added a deterministic socket timeout regression test.
- Existing HTTP error handling and connection cleanup were retained.

### Verification

- HTTP 500 test: PASS.
- Read timeout test: PASS.
- Gradle result: BUILD SUCCESSFUL.
- XML test report confirms both tests passed.

### Verification Limitation

The timeout was simulated with FakeHttpURLConnection. An actual physical-device network timeout was not independently reproduced.

### Final Result

**HTTP 500 and simulated network timeout handling verified. No confirmed unhandled crash.**

---

## Week 2 Day 8 — Bug 3: UI Not Updating After Refresh

**Date:** 26 November 2026

### Reproduction

1. Opened the Employee Task Manager Task Details screen.
2. Confirmed that 200 API tasks were displayed.
3. Enabled Airplane Mode and then restored network connectivity.
4. Selected the API Retry button.

### Observations

- Loading message appeared after Retry.
- A new GET `/todos` request appeared in Android Studio Network Inspector.
- HTTP status: 200.
- Response type: application/json.
- Response size: 23.7 KB.
- The API RecyclerView displayed all 200 tasks again.

### Root Cause

The reported UI refresh failure could not be reproduced during this verification. The existing refresh flow completed successfully.

No confirmed root cause for the reported defect was established.

### Resolution

- No production-code modification was required.
- Existing Retry, ViewModel StateFlow collection, and adapter submission were retained.

### Verification Evidence

- Retry action: successful.
- Loading state: displayed.
- New GET `/todos` request: captured.
- HTTP 200 response: verified.
- 200 API tasks: displayed again.

### Saved Screenshot

`docs/week 2/day 8/screenshots/03-refresh-recovery-http-200.png`

### Verification Limitation

The reported refresh defect was not reproduced. The successful manual recovery sequence confirms the observed refresh path, but does not establish that every possible refresh edge case is defect-free.

### Final Result

**Refresh recovery verified. No confirmed UI refresh defect or production fix established.**

---

## Week 2 Day 8 — Combined API Client Test Results

**Date:** 26 November 2026

### Test Execution

Command: `.\gradlew.bat :app:testDebugUnitTest --tests "com.example.employeetaskmanager.TaskApiClientTest"`

### Results

- Total tests: 9
- Passed: 9
- Failures: 0
- Errors: 0
- Skipped: 0
- Gradle result: BUILD SUCCESSFUL.

### Coverage

- Successful API response.
- HTTP 204 and HTTP 503.
- HTTP 500 Internal Server Error.
- Malformed JSON.
- Missing required field.
- Blank response body.
- Network IOException.
- Simulated read timeout.

### Conclusion

All nine TaskApiClient tests passed. Bug 1 and Bug 2 error-handling scenarios are covered by regression tests. Bug 3 refresh recovery was manually verified on the physical device; the reported defect was not reproduced.

The simulated timeout does not establish actual physical-network timeout behavior.

### Debugger Follow-up Evidence — Bug 1

**Date:** 26 November 2026

- Breakpoint observed at `TaskApiClient.kt:72`.
- Test: `fetchTasks_malformedJson_returnsFailureAndDisconnects()`.
- Malformed JSON reached `JSONArray(responseBody)`.
- JSON parsing raised a `JSONException` or parsing exception.
- Existing catch block returned `Result.failure(exception)`.
- Existing finally block disconnected the HTTP connection.
- Debug test result: 1 passed, 0 failed.

**Conclusion:** Malformed JSON parser failure and exception-handling path verified through debugger observation and unit testing. Physical-device crash was not established.
