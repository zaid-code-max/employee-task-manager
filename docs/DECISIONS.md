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
