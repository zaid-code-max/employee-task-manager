# Employee Task Manager — Storage Design

## 1. Storage Responsibility

The Storage layer is responsible for providing persistent task data to the Repository.

The Storage layer will:
- Create and store tasks.
- Retrieve stored tasks.
- Update task completion state.
- Preserve task identity.
- Provide the current task state when requested.

The Storage layer should not contain UI logic.

## 2. Storage Boundary

The Repository is the only application layer that communicates directly with Storage.

The application flow remains:

Screen -> ViewModel -> Repository -> Storage

The Screen and ViewModel must not directly access Storage.

## 3. Task Operations

### Create Task

Store a new task with:
- A unique task ID.
- A non-empty task title.
- isCompleted = false.

### Read Tasks

Return the currently stored tasks.

### Update Completion

Update the isCompleted value for an existing task.

### Retrieve Task State

Return the current completion state of a task so that the UI can display the correct status.

## 4. Data Consistency

- Every task must have a unique identity.
- A new task starts in the Pending state.
- Completing a task changes its state to Completed.
- The stored completion state must remain the source of truth.
- Repeating the completion action on an already completed task must not create an invalid state.

## 5. Empty Storage

If no tasks are stored, Storage should return an empty task collection rather than treating the condition as an error.

The ViewModel and Screen can then handle the empty-task state appropriately.

## 6. Error Handling

Storage errors must not be exposed directly to the Screen.

The Repository should provide a controlled result to the ViewModel, and the ViewModel should expose an appropriate UI state.

## 7. Technology Decision

The Day 3 requirements and architecture define the Storage layer but do not mandate a specific storage technology.

The storage technology will be selected during implementation based on:
- Project requirements.
- Android compatibility.
- Maintainability.
- Reliability.
- Appropriate separation of concerns.

The selected technology must preserve the approved architecture.

## 8. Change Isolation

The Screen and ViewModel should not depend on the implementation details of Storage.

If the Storage implementation changes in the future, the Repository should act as the boundary that limits changes to the rest of the application.

## 9. Professional Design Rule

Storage should be treated as an independent data layer rather than placing persistence logic inside an Activity.

This keeps the application easier to test, maintain, and extend.
