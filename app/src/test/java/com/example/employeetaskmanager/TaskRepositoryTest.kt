package com.example.employeetaskmanager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRepositoryTest {

    @Test
    fun allTasks_returnsDaoTasks() = runTest {
        val initialTasks = listOf(TaskEntity(id = 1L, title = "Task 1"))
        val fakeDao = TestFakeTaskDao(initialTasks)
        val fakeApi = TestFakeApiDataSource(Result.success(emptyList()))
        val repository = TaskRepository(fakeDao, fakeApi)

        val tasks = repository.allTasks.first()
        assertEquals(initialTasks, tasks)
    }

    @Test
    fun insertTask_delegatesToDao() = runTest {
        val fakeDao = TestFakeTaskDao()
        val fakeApi = TestFakeApiDataSource(Result.success(emptyList()))
        val repository = TaskRepository(fakeDao, fakeApi)

        val newTask = TaskEntity(title = "New Task")
        repository.insertTask(newTask)

        val tasks = repository.allTasks.first()
        assertEquals(1, tasks.size)
        assertEquals("New Task", tasks[0].title)
    }

    @Test
    fun updateTaskCompletion_delegatesToDao() = runTest {
        val initialTasks = listOf(TaskEntity(id = 1L, title = "Task 1", isCompleted = false))
        val fakeDao = TestFakeTaskDao(initialTasks)
        val fakeApi = TestFakeApiDataSource(Result.success(emptyList()))
        val repository = TaskRepository(fakeDao, fakeApi)

        repository.updateTaskCompletion(1L, true)

        val tasks = repository.allTasks.first()
        assertTrue(tasks[0].isCompleted)
    }

    @Test
    fun deleteTask_delegatesToDao() = runTest {
        val initialTasks = listOf(TaskEntity(id = 1L, title = "Task 1"))
        val fakeDao = TestFakeTaskDao(initialTasks)
        val fakeApi = TestFakeApiDataSource(Result.success(emptyList()))
        val repository = TaskRepository(fakeDao, fakeApi)

        repository.deleteTask(1L)

        val tasks = repository.allTasks.first()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun fetchApiTasks_success_returnsTasks() = runTest {
        val apiTasks = listOf(ApiTaskDto(userId = 1, id = 10L, title = "API Task", completed = false))
        val fakeDao = TestFakeTaskDao()
        val fakeApi = TestFakeApiDataSource(Result.success(apiTasks))
        val repository = TaskRepository(fakeDao, fakeApi)

        val result = repository.fetchApiTasks()
        assertTrue(result.isSuccess)
        assertEquals(apiTasks, result.getOrThrow())
    }

    @Test
    fun fetchApiTasks_failure_preservesException() = runTest {
        val exception = RuntimeException("API Error")
        val fakeDao = TestFakeTaskDao()
        val fakeApi = TestFakeApiDataSource(Result.failure(exception))
        val repository = TaskRepository(fakeDao, fakeApi)

        val result = repository.fetchApiTasks()
        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }
}

private class TestFakeTaskDao(
    initialTasks: List<TaskEntity> = emptyList()
) : TaskDao {
    private val tasks = MutableStateFlow(initialTasks)
    private var nextId = (initialTasks.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasks

    override suspend fun insertTask(task: TaskEntity) {
        val savedTask = if (task.id == 0L) task.copy(id = nextId++) else task
        tasks.value = tasks.value.filterNot { it.id == savedTask.id } + savedTask
    }

    override suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        tasks.value = tasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = isCompleted) else it
        }
    }

    override suspend fun deleteTask(taskId: Long) {
        tasks.value = tasks.value.filterNot { it.id == taskId }
    }
}

private class TestFakeApiDataSource(
    private val response: Result<List<ApiTaskDto>>
) : TaskApiDataSource {
    override suspend fun fetchTasks(): Result<List<ApiTaskDto>> = response
}
