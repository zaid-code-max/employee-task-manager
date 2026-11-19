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
                viewModel.allTasks.collect { tasks ->
                    taskAdapter.submitList(tasks)

                    val total = tasks.size
                    val completed = tasks.count { it.isCompleted }
                    val pending = total - completed

                    textViewTotalCount.text = total.toString()
                    textViewPendingCount.text = pending.toString()
                    textViewCompletedCount.text = completed.toString()
                    textViewTaskCountBadge.text = "$total tasks"

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
