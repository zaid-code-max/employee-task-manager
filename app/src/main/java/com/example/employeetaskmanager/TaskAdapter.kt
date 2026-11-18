package com.example.employeetaskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskCheckedChange: (TaskEntity, Boolean) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, onTaskCheckedChange)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxTask)
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTaskTitle)
        private val statusTextView: TextView = itemView.findViewById(R.id.textViewTaskStatus)

        fun bind(task: TaskEntity, onTaskCheckedChange: (TaskEntity, Boolean) -> Unit) {
            titleTextView.text = task.title
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted

            if (task.isCompleted) {
                statusTextView.text = itemView.context.getString(R.string.status_completed)
                statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                // Once completed, task cannot be unchecked
                checkBox.isEnabled = false
            } else {
                statusTextView.text = itemView.context.getString(R.string.status_pending)
                statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                checkBox.isEnabled = true
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (task.isCompleted != isChecked) {
                    onTaskCheckedChange(task, isChecked)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
