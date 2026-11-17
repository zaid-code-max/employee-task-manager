# Employee Task Manager — Storage Technology Decision

## Decision

The application will use **Room Persistence Library** as the Storage implementation.

## Reason for Selection

Room is selected because it provides:

- Persistent local storage for task data.
- A structured database layer for the Task entity.
- Reliable task creation, retrieval, and completion updates.
- Clear separation between Storage and Repository.
- Good maintainability as the application grows.

## Architecture Integration

The selected Storage technology will follow the approved architecture:

Screen -> ViewModel -> Repository -> Room Storage

The Screen and ViewModel will not access Room directly.

The Repository will act as the boundary between the ViewModel and Room.

## Task Data

Room Storage will persist the following Task fields:

- id: Long
- title: String
- isCompleted: Boolean

## Business Rules

- Every task must have a unique ID.
- Task title must not be empty or blank.
- New tasks start as Pending.
- Completed tasks remain Completed.
- The stored completion state is the source of truth.
- Repeating completion on an already completed task must be handled safely.

## Scope

This decision defines the Storage technology for implementation.

The implementation of Room components will be completed in a later implementation step.

## Change Control

The Storage implementation must preserve the approved:

Screen -> ViewModel -> Repository -> Storage

architecture.

