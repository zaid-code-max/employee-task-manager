package com.example.employeetaskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL, PENDING, COMPLETED
}

sealed class ApiTaskUiState {
    object Idle : ApiTaskUiState()
    object Loading : ApiTaskUiState()
    data class Success(val tasks: List<ApiTaskDto>) : ApiTaskUiState()
    object Empty : ApiTaskUiState()
    data class Error(val message: String) : ApiTaskUiState()
}

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _currentFilter = MutableStateFlow(TaskFilter.ALL)
    val currentFilter: StateFlow<TaskFilter> = _currentFilter

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(repository.allTasks, _currentFilter) { tasks, filter ->
        when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _apiTaskState = MutableStateFlow<ApiTaskUiState>(ApiTaskUiState.Idle)
    val apiTaskState: StateFlow<ApiTaskUiState> = _apiTaskState

    fun loadApiTasks(forceRefresh: Boolean = false) {
        val currentState = _apiTaskState.value

        if (currentState is ApiTaskUiState.Loading) return
        if (!forceRefresh && currentState !is ApiTaskUiState.Idle) return

        viewModelScope.launch {
            _apiTaskState.value = ApiTaskUiState.Loading

            val result = repository.fetchApiTasks()

            _apiTaskState.value = result.fold(
                onSuccess = { tasks ->
                    if (tasks.isEmpty()) {
                        ApiTaskUiState.Empty
                    } else {
                        ApiTaskUiState.Success(tasks)
                    }
                },
                onFailure = { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    ApiTaskUiState.Error(
                        exception.message ?: "Unable to load API tasks. Please try again."
                    )
                }
            )
        }
    }

    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
    }

    fun insertTask(title: String, priority: String = "Medium") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(TaskEntity(title = title.trim(), priority = priority))
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
