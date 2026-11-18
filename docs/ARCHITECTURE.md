# Employee Task Manager — Architecture

## Architecture Flow

Screen -> ViewModel -> Repository -> Storage

## 1. Screen

The Screen is responsible for:
- Displaying the task list.
- Accepting task input from the employee.
- Providing an action to add a task.
- Providing an action to mark a task as complete.
- Displaying the current task status.
- Showing the appropriate empty-task state.

The Screen should not directly manage task storage.

## 2. ViewModel

The ViewModel is responsible for:
- Managing task-related UI state.
- Receiving task actions from the Screen.
- Requesting task operations through the Repository.
- Exposing the current task state to the Screen.

The ViewModel should not directly access the Storage layer.

## 3. Repository

The Repository is responsible for:
- Providing a single access point for task data operations.
- Coordinating communication between the ViewModel and Storage.
- Providing task data to the ViewModel.
- Sending task creation and completion operations to Storage.

The Screen should not directly communicate with the Repository.

## 4. Storage

The Storage layer is responsible for:
- Storing task data.
- Retrieving task data.
- Updating task completion state.
- Providing persistent task data to the Repository.

The selected Storage implementation is the Room Persistence Library.

## 5. Data Flow

### View Tasks

Screen -> ViewModel -> Repository -> Storage

Storage -> Repository -> ViewModel -> Screen

### Add Task

Screen -> ViewModel -> Repository -> Storage

### Mark Task Complete

Screen -> ViewModel -> Repository -> Storage

Storage -> Repository -> ViewModel -> Screen

### View Task Status

Storage -> Repository -> ViewModel -> Screen

## 6. Existing Application Structure

The existing application contains:
- MainActivity
- TaskDetailsActivity
- Home screen layout
- Task Details screen layout

Existing Home -> Task Details navigation should be preserved unless the approved implementation requires a change.

## 7. Architecture Constraints

- The Screen must not directly access Storage.
- The ViewModel must not directly access Storage.
- The Repository acts as the data-access boundary for the ViewModel.
- Task state should have a consistent source of truth.
- Existing working functionality should not be removed unnecessarily.
- Implementation should follow the approved architecture before code changes begin.

## 8. Room Storage Implementation

The Storage layer is implemented using the Room Persistence Library.

### TaskEntity
- Defines the `tasks` database table.
- Stores the task ID, title, and completion state.
- Generates unique task IDs automatically.

### TaskDao
- `getAllTasks()` retrieves tasks as a Flow.
- `insertTask()` inserts a task.
- `updateTaskCompletion()` updates the completion state.

### AppDatabase
- Provides the Room database instance.
- Uses a singleton database instance.
- Database name: `task_database`.

## 9. Dependency Creation

`TaskDetailsActivity` obtains `TaskViewModel` using the `by viewModels` delegate and `TaskViewModelFactory`.

The factory:
1. Obtains the Room database instance.
2. Retrieves `TaskDao`.
3. Creates `TaskRepository`.
4. Supplies the Repository to `TaskViewModel`.

The ViewModel receives the Repository through its constructor and does not directly access Room, AppDatabase, or TaskDao.

## 10. Architecture Diagram

The Mermaid architecture diagram is maintained at:

`docs/diagrams/architecture.mmd`

It documents the runtime task-data flow and the ViewModel dependency-creation flow.

## 11. Connection-by-Connection Explanation

This section explains every connection shown in `docs/diagrams/architecture.mmd`.

### A. Runtime Task Data Flow

#### 1. Screen -> ViewModel

- When the employee taps the Add Task button, `TaskDetailsActivity` calls `TaskViewModel.insertTask(title)`.
- When the employee changes a task checkbox, `TaskAdapter` invokes the callback that calls `TaskViewModel.updateTaskCompletion(task.id, isCompleted)`.
- The Screen sends user actions to the ViewModel instead of accessing the database directly.

#### 2. ViewModel -> Repository

- `TaskViewModel` calls `TaskRepository.insertTask()` to create a task.
- It calls `TaskRepository.updateTaskCompletion()` to update task completion.
- It observes `TaskRepository.allTasks` to receive task data.

#### 3. Repository -> TaskDao

- `TaskRepository` delegates task retrieval to `TaskDao.getAllTasks()`.
- It delegates task insertion to `TaskDao.insertTask()`.
- It delegates completion updates to `TaskDao.updateTaskCompletion()`.

#### 4. TaskDao -> AppDatabase

- Room executes the DAO query and database operations.
- `getAllTasks()` runs `SELECT * FROM tasks`.
- `insertTask()` inserts a task using the configured conflict strategy.
- `updateTaskCompletion()` updates the `isCompleted` value for the specified task ID.

#### 5. AppDatabase -> TaskDao -> Repository

- Room provides the task records returned by the DAO query.
- `getAllTasks()` exposes the query results as `Flow<List<TaskEntity>>`.
- Room updates the observable query results when the tasks table changes.

#### 6. Repository -> ViewModel

- `TaskRepository` exposes the DAO task stream through its `allTasks` property.
- `TaskViewModel` converts this Flow into `StateFlow<List<TaskEntity>>` using `stateIn()`.

#### 7. ViewModel -> Screen

- `TaskViewModel.allTasks` exposes the current task list as StateFlow.
- `TaskDetailsActivity` collects this state using `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- The Activity submits updated tasks to `TaskAdapter`.
- The Activity also updates the empty-state visibility according to whether the task list is empty.

#### 8. TaskEntity -> AppDatabase

- `TaskEntity` represents the `tasks` database table.
- Its fields define task identity, title, and completion state.
- The entity is registered in the Room `AppDatabase`.

### B. ViewModel Dependency Creation Flow

This flow creates the dependencies required by the ViewModel. It is separate from normal task operations.

#### 9. Screen -> TaskViewModelFactory

- `TaskDetailsActivity` uses the `by viewModels` delegate.
- It supplies `TaskViewModelFactory(applicationContext)` to create the ViewModel.

#### 10. TaskViewModelFactory -> AppDatabase

- The Factory calls `AppDatabase.getDatabase(context)`.
- The database provides the application's Room database instance.

#### 11. AppDatabase -> TaskDao

- The Factory calls `database.taskDao()`.
- This obtains the DAO used for task data operations.

#### 12. TaskDao -> TaskRepository

- The Factory creates `TaskRepository(database.taskDao())`.
- The Repository receives the DAO through its constructor.

#### 13. TaskRepository -> TaskViewModel

- The Factory creates `TaskViewModel(repository)`.
- The ViewModel receives the Repository through constructor injection.

#### 14. Factory-created ViewModel -> Runtime ViewModel

- The Factory returns the requested `TaskViewModel` instance to the ViewModel provider.
- The Screen receives that instance through the `by viewModels` delegate.
- The ViewModel then participates in the runtime task data flow described above.

### C. Architecture Boundary Summary

- The Screen communicates with the ViewModel.
- The ViewModel communicates with the Repository.
- The Repository performs task-related data operations through TaskDao.
- Room manages persistent task data.
- The Factory creates and supplies dependencies; it does not replace the Repository as the task data-access boundary.
