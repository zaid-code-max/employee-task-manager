# Employee Task Manager — Day 8 Debugging Summary

## Overview
This document records the reproduction, observation, isolation, and verification of the three defects for Day 8 (Week 2):
1. Malformed data crash during JSON parsing.
2. HTTP 500 internal server error and read timeout handling.
3. UI not updating correctly after task refresh.

---

## Defect 1: Malformed Data Crash
- **Reproduction:** Injected malformed/incomplete JSON response (`[{"userId":1,"id":1,"title":`) into `TaskApiClient`.
- **Observation:** Verified via breakpoint at `TaskApiClient.kt` line 72 (`val jsonArray = JSONArray(responseBody)`).
- **Isolation:** Unhandled JSON parsing exceptions caused ungraceful termination during list parsing.
- **Verification:** Implemented `fetchTasks_malformedJson_returnsFailureAndDisconnects()` in `TaskApiClientTest.kt`. Exception caught and returned as `Result.failure()` with connection cleanup.

---

## Defect 2: HTTP 500 / Read Timeout
- **Reproduction:** Simulated HTTP 500 Internal Server Error and `SocketTimeoutException` using `FakeHttpURLConnection`.
- **Observation:** Verified that server errors return `ApiHttpException` with status code 500 and timeout exceptions return `SocketTimeoutException`.
- **Isolation:** Missing error stream handling and timeout exception propagation in network client.
- **Verification:** Implemented `fetchTasks_internalServerError_returnsHttpExceptionAndDisconnects()` and `fetchTasks_readTimeout_returnsFailureAndDisconnects()` in `TaskApiClientTest.kt`. All tests passed successfully.

---

## Defect 3: UI Not Updating After Refresh
- **Reproduction:** Refreshed remote tasks and verified reactive UI collection.
- **Observation:** `TaskViewModel` and `TaskDetailsActivity` use lifecycle-aware collection (`repeatOnLifecycle(STARTED)`) and `StateFlow` to immediately submit updated lists to `TaskAdapter`.
- **Verification:** Unit tests and device testing confirmed real-time list rendering and dashboard counter updates.
