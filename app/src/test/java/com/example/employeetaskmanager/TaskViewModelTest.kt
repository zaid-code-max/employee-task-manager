package com.example.employeetaskmanager

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        api: TaskApiDataSource = FakeApiDataSource(Result.success(emptyList())),
        dao: TaskDao = FakeTaskDao()
    ): TaskViewModel {
        return TaskViewModel(TaskRepository(dao, api))
    }

    @Test
    fun initialApiTaskState_isIdle() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertEquals(ApiTaskUiState.Idle, viewModel.apiTaskState.value)
    }

    @Test
    fun loadApiTasks_success_returnsSuccessState() = runTest(testDispatcher) {
        val tasks = listOf(
            ApiTaskDto(1, 10L, "Prepare report", false),
            ApiTaskDto(2, 20L, "Review project", true)
        )
        val api = FakeApiDataSource(Result.success(tasks))
        val viewModel = createViewModel(api)

        viewModel.loadApiTasks()
        advanceUntilIdle()

        assertEquals(ApiTaskUiState.Success(tasks), viewModel.apiTaskState.value)
        assertEquals(1, api.callCount)
    }

    @Test
    fun loadApiTasks_emptyResponse_returnsEmptyState() = runTest(testDispatcher) {
        val api = FakeApiDataSource(Result.success(emptyList()))
        val viewModel = createViewModel(api)

        viewModel.loadApiTasks()
        advanceUntilIdle()

        assertEquals(ApiTaskUiState.Empty, viewModel.apiTaskState.value)
    }

    @Test
    fun loadApiTasks_failure_returnsErrorState() = runTest(testDispatcher) {
        val api = FakeApiDataSource(
            Result.failure(IllegalStateException("Network unavailable"))
        )
        val viewModel = createViewModel(api)

        viewModel.loadApiTasks()
        advanceUntilIdle()

        assertEquals(
            ApiTaskUiState.Error("Network unavailable"),
            viewModel.apiTaskState.value
        )
    }

    @Test
    fun loadApiTasks_emitsLoadingBeforeResult() = runTest(testDispatcher) {
        val response = CompletableDeferred<Result<List<ApiTaskDto>>>()
        val api = DeferredFakeApiDataSource(response)
        val viewModel = createViewModel(api)

        viewModel.loadApiTasks()
        runCurrent()

        assertEquals(ApiTaskUiState.Loading, viewModel.apiTaskState.value)

        response.complete(Result.success(emptyList()))
        advanceUntilIdle()

        assertEquals(ApiTaskUiState.Empty, viewModel.apiTaskState.value)
    }

    @Test
    fun loadApiTasks_whileLoading_ignoresDuplicateRequest() = runTest(testDispatcher) {
        val response = CompletableDeferred<Result<List<ApiTaskDto>>>()
        val api = DeferredFakeApiDataSource(response)
        val viewModel = createViewModel(api)

        viewModel.loadApiTasks()
        runCurrent()

        viewModel.loadApiTasks()
        runCurrent()

        assertEquals(1, api.callCount)

        response.complete(Result.success(emptyList()))
        advanceUntilIdle()

        assertEquals(ApiTaskUiState.Empty, viewModel.apiTaskState.value)
    }

    @Test
    fun loadApiTasks_afterSuccessWithoutForceRefresh_doesNotFetchAgain() =
        runTest(testDispatcher) {
            val api = FakeApiDataSource(Result.success(emptyList()))
            val viewModel = createViewModel(api)

            viewModel.loadApiTasks()
            advanceUntilIdle()

            viewModel.loadApiTasks()
            advanceUntilIdle()

            assertEquals(1, api.callCount)
        }

    @Test
    fun loadApiTasks_forceRefresh_fetchesAgain() = runTest(testDispatcher) {
        val api = FakeApiDataSource(Result.success(emptyList()))
        val viewModel = createViewModel(api)

        viewModel.loadApiTasks()
        advanceUntilIdle()

        viewModel.loadApiTasks(forceRefresh = true)
        advanceUntilIdle()

        assertEquals(2, api.callCount)
    }

    @Test
    fun setFilter_pending_showsOnlyPendingTasks() = runTest(testDispatcher) {
        val pending = TaskEntity(id = 1L, title = "Pending task")
        val completed = TaskEntity(
            id = 2L,
            title = "Completed task",
            isCompleted = true
        )
        val dao = FakeTaskDao(listOf(pending, completed))
        val viewModel = createViewModel(dao = dao)

        advanceUntilIdle()
        viewModel.setFilter(TaskFilter.PENDING)
        advanceUntilIdle()

        assertEquals(listOf(pending), viewModel.filteredTasks.value)
    }
}

private class FakeApiDataSource(
    var response: Result<List<ApiTaskDto>>
) : TaskApiDataSource {

    var callCount = 0
        private set

    override suspend fun fetchTasks(): Result<List<ApiTaskDto>> {
        callCount++
        return response
    }
}

private class DeferredFakeApiDataSource(
    private val response: CompletableDeferred<Result<List<ApiTaskDto>>>
) : TaskApiDataSource {

    var callCount = 0
        private set

    override suspend fun fetchTasks(): Result<List<ApiTaskDto>> {
        callCount++
        return response.await()
    }
}

private class FakeTaskDao(
    initialTasks: List<TaskEntity> = emptyList()
) : TaskDao {

    private val tasks = MutableStateFlow(initialTasks)
    private var nextId = (initialTasks.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasks

    override suspend fun insertTask(task: TaskEntity) {
        val savedTask = if (task.id == 0L) {
            task.copy(id = nextId++)
        } else {
            task
        }

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
