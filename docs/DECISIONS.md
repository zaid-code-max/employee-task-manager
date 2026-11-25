# Employee Task Manager — Architectural & Technical Decisions

## 1. Storage Technology Decision
- **Decision:** Room Persistence Library (`androidx.room`) with SQLite.
- **Rationale:** Provides compile-time query verification, reactive Flow support out-of-the-box, type-safe data access objects (DAOs), and robust local persistence suited for Android architecture guidelines.

## 2. Architecture Pattern
- **Decision:** Clean Architecture / MVVM (`Screen -> ViewModel -> Repository -> Storage`).
- **Rationale:** Ensures clear separation of concerns, testability, and reactive state management between UI and local persistence.

## 3. Dependency Injection & State Management
- **Decision:** AndroidX ViewModel with Kotlin Coroutines and StateFlow (`StateIn`).
- **Rationale:** Lightweight, lifecycle-aware state observation without requiring heavy external DI libraries for this stage.

## 4. Remote REST API Integration

- **Decision:** Use `TaskApiClient` with `HttpURLConnection` to retrieve remote tasks through `GET https://jsonplaceholder.typicode.com/todos`.
- **Rationale:** Provides a direct HTTP implementation using the existing Android and Java networking APIs without adding a separate Retrofit or OkHttp dependency.
- **Response model:** Represent API records using `ApiTaskDto` containing `userId`, `id`, `title`, and `completed`.

## 5. Local and Remote Data Separation

- **Decision:** Keep remote API tasks separate from locally stored Room tasks.
- **Rationale:** Prevents remote sample records from changing existing local task data, priorities, dashboard statistics, or task operations.
- **Limitation:** Remote tasks are read-only. No Room caching, synchronization, or remote write operations are implemented.

## 6. Remote API UI State

- **Decision:** Expose API retrieval states through `TaskViewModel.apiTaskState` using `StateFlow`.
- **States:** Idle, Loading, Success, Empty, and Error.
- **Rationale:** Allows `TaskDetailsActivity` to render API loading, content, empty, and failure states independently of the local Room task list.
- **Retry:** A retry action invokes `loadApiTasks(forceRefresh = true)`.
