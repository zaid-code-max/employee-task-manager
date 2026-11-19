package com.example.employeetaskmanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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
        val textViewTaskCountBadge = findViewById<TextView>(R.id.textViewTaskCountBadge)

        val btnFilterAll = findViewById<Button>(R.id.btnFilterAll)
        val btnFilterPending = findViewById<Button>(R.id.btnFilterPending)
        val btnFilterCompleted = findViewById<Button>(R.id.btnFilterCompleted)

        val priorities = arrayOf("Low", "Medium", "High")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinnerPriority.adapter = spinnerAdapter
        spinnerPriority.setSelection(1) // Default to Medium

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

        fun updateFilterButtons(selected: TaskFilter) {
            val activeColor = getColor(R.color.primary_indigo)
            val inactiveColor = getColor(R.color.card_dark)
            val activeText = getColor(R.color.text_white)
            val inactiveText = getColor(R.color.text_muted)

            btnFilterAll.backgroundTintList = android.content.res.ColorStateList.valueOf(if (selected == TaskFilter.ALL) activeColor else inactiveColor)
            btnFilterAll.setTextColor(if (selected == TaskFilter.ALL) activeText else inactiveText)

            btnFilterPending.backgroundTintList = android.content.res.ColorStateList.valueOf(if (selected == TaskFilter.PENDING) activeColor else inactiveColor)
            btnFilterPending.setTextColor(if (selected == TaskFilter.PENDING) activeText else inactiveText)

            btnFilterCompleted.backgroundTintList = android.content.res.ColorStateList.valueOf(if (selected == TaskFilter.COMPLETED) activeColor else inactiveColor)
            btnFilterCompleted.setTextColor(if (selected == TaskFilter.COMPLETED) activeText else inactiveText)
        }

        btnFilterAll.setOnClickListener {
            viewModel.setFilter(TaskFilter.ALL)
            updateFilterButtons(TaskFilter.ALL)
        }

        btnFilterPending.setOnClickListener {
            viewModel.setFilter(TaskFilter.PENDING)
            updateFilterButtons(TaskFilter.PENDING)
        }

        btnFilterCompleted.setOnClickListener {
            viewModel.setFilter(TaskFilter.COMPLETED)
            updateFilterButtons(TaskFilter.COMPLETED)
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect total tasks for statistics counts
                launch {
                    viewModel.allTasks.collect { tasks ->
                        val total = tasks.size
                        val completed = tasks.count { it.isCompleted }
                        val pending = total - completed

                        textViewTotalCount.text = total.toString()
                        textViewPendingCount.text = pending.toString()
                        textViewCompletedCount.text = completed.toString()
                        textViewTaskCountBadge.text = "$total tasks"
                    }
                }

                // Collect filtered tasks for the RecyclerView
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
            }
        }
    }
}
