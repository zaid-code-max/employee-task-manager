package com.example.employeetaskmanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class TaskDetailsActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(applicationContext)
    }

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var apiTaskAdapter: ApiTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val editTextTaskTitle = findViewById<EditText>(R.id.editTextTaskTitle)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val recyclerViewTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val textViewEmptyState = findViewById<View>(R.id.textViewEmptyState)
        val textViewTotalCount = findViewById<TextView>(R.id.textViewTotalCount)
        val textViewPendingCount = findViewById<TextView>(R.id.textViewPendingCount)
        val textViewCompletedCount = findViewById<TextView>(R.id.textViewCompletedCount)

        val progressBarApiTasks = findViewById<ProgressBar>(R.id.progressBarApiTasks)
        val textViewApiTaskStatus = findViewById<TextView>(R.id.textViewApiTaskStatus)
        val buttonRetryApiTasks = findViewById<Button>(R.id.buttonRetryApiTasks)
        val recyclerViewApiTasks = findViewById<RecyclerView>(R.id.recyclerViewApiTasks)

        val btnFilterAll = findViewById<Button>(R.id.btnFilterAll)
        val btnFilterPending = findViewById<Button>(R.id.btnFilterPending)
        val btnFilterCompleted = findViewById<Button>(R.id.btnFilterCompleted)

        val priorities = arrayOf("Low", "Medium", "High")
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priorities
        )
        spinnerPriority.adapter = spinnerAdapter
        spinnerPriority.setSelection(1)

        taskAdapter = TaskAdapter(
            onTaskCheckedChange = { task, isCompleted ->
                viewModel.updateTaskCompletion(task.id, isCompleted)
            },
            onTaskDeleted = { task ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete \"${task.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteTask(task.id)
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        apiTaskAdapter = ApiTaskAdapter()
        recyclerViewApiTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewApiTasks.adapter = apiTaskAdapter

        buttonRetryApiTasks.setOnClickListener {
            viewModel.loadApiTasks(forceRefresh = true)
        }

        fun updateFilterButtons(selected: TaskFilter) {
            val activeColor = getColor(R.color.primary_indigo)
            val inactiveColor = getColor(R.color.card_dark)
            val activeText = getColor(R.color.text_white)
            val inactiveText = getColor(R.color.text_muted)

            btnFilterAll.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    if (selected == TaskFilter.ALL) activeColor else inactiveColor
                )
            btnFilterAll.setTextColor(
                if (selected == TaskFilter.ALL) activeText else inactiveText
            )

            btnFilterPending.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    if (selected == TaskFilter.PENDING) activeColor else inactiveColor
                )
            btnFilterPending.setTextColor(
                if (selected == TaskFilter.PENDING) activeText else inactiveText
            )

            btnFilterCompleted.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    if (selected == TaskFilter.COMPLETED) activeColor else inactiveColor
                )
            btnFilterCompleted.setTextColor(
                if (selected == TaskFilter.COMPLETED) activeText else inactiveText
            )
        }

        btnFilterAll.setOnClickListener {
            viewModel.setFilter(TaskFilter.ALL)
        }

        btnFilterPending.setOnClickListener {
            viewModel.setFilter(TaskFilter.PENDING)
        }

        btnFilterCompleted.setOnClickListener {
            viewModel.setFilter(TaskFilter.COMPLETED)
        }

        buttonAddTask.setOnClickListener {
            val title = editTextTaskTitle.text.toString()
            val priority = spinnerPriority.selectedItem.toString()

            if (title.isBlank()) {
                Toast.makeText(this, R.string.error_empty_title, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertTask(title, priority)
                editTextTaskTitle.setText("")
                spinnerPriority.setSelection(1)
            }
        }

        // Load API tasks once. ViewModel retains the state across Activity recreation.
        viewModel.loadApiTasks()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.currentFilter.collect { filter ->
                        updateFilterButtons(filter)
                    }
                }

                // Local Room task statistics.
                launch {
                    viewModel.allTasks.collect { tasks ->
                        val total = tasks.size
                        val completed = tasks.count { it.isCompleted }
                        val pending = total - completed

                        textViewTotalCount.text = total.toString()
                        textViewPendingCount.text = pending.toString()
                        textViewCompletedCount.text = completed.toString()
                    }
                }

                // Local Room task list.
                launch {
                    viewModel.filteredTasks.collect { tasks ->
                        taskAdapter.submitList(tasks)

                        if (tasks.isEmpty()) {
                            recyclerViewTasks.visibility = View.GONE
                            textViewEmptyState.visibility = View.VISIBLE
                        } else {
                            recyclerViewTasks.visibility = View.VISIBLE
                            textViewEmptyState.visibility = View.GONE
                        }
                    }
                }

                // Independent REST API task list and UI states.
                launch {
                    viewModel.apiTaskState.collect { state ->
                        when (state) {
                            is ApiTaskUiState.Idle -> {
                                progressBarApiTasks.visibility = View.GONE
                                textViewApiTaskStatus.text = "Waiting to load API tasks..."
                                buttonRetryApiTasks.visibility = View.GONE
                                recyclerViewApiTasks.visibility = View.GONE
                            }

                            is ApiTaskUiState.Loading -> {
                                progressBarApiTasks.visibility = View.VISIBLE
                                textViewApiTaskStatus.text = "Loading tasks from API..."
                                buttonRetryApiTasks.visibility = View.GONE
                                recyclerViewApiTasks.visibility = View.GONE
                            }

                            is ApiTaskUiState.Success -> {
                                progressBarApiTasks.visibility = View.GONE
                                textViewApiTaskStatus.text =
                                    "Loaded ${state.tasks.size} tasks from API."
                                buttonRetryApiTasks.visibility = View.GONE

                                apiTaskAdapter.submitList(state.tasks)
                                recyclerViewApiTasks.visibility = View.VISIBLE
                            }

                            is ApiTaskUiState.Empty -> {
                                progressBarApiTasks.visibility = View.GONE
                                textViewApiTaskStatus.text = "No API tasks were returned."
                                buttonRetryApiTasks.visibility = View.VISIBLE
                                apiTaskAdapter.submitList(emptyList())
                                recyclerViewApiTasks.visibility = View.GONE
                            }

                            is ApiTaskUiState.Error -> {
                                progressBarApiTasks.visibility = View.GONE
                                textViewApiTaskStatus.text =
                                    "Unable to load API tasks: ${state.message}"
                                buttonRetryApiTasks.visibility = View.VISIBLE
                                recyclerViewApiTasks.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }
}
