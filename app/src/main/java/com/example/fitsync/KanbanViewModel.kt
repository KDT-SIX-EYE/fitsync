package com.example.fitsync.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitsync.data.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class KanbanViewModel : ViewModel() {
    private val firestore = Firebase.firestore

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }



    private suspend fun fetchTasks(): List<Task> {
        val tasksSnapshot = firestore.collection("tasks").get().await()
        return tasksSnapshot.documents.mapNotNull { doc ->
            val data = doc.data
            val id = doc.id
            if (data != null) {
                Task(id, data["title"] as String, data["description"] as String, data["status"] as String)
            } else {
                null
            }
        }
    }


    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            val taskRef = firestore.collection("tasks").document(taskId)

            try {
                val updateData = mapOf(
                    "status" to newStatus
                )
                taskRef.update(updateData).await()

                val updatedTasks = _tasks.value.map { task ->
                    if (task.id == taskId) {
                        task.copy(status = newStatus)
                    } else {
                        task
                    }
                }
                _tasks.value = updatedTasks
            } catch (e: Exception) {
                println("Error updating status of task with ID: $taskId")
            }
        }
    }

    fun updateTask(taskId: String, updatedTask: Task) {
        viewModelScope.launch {
            val taskRef = firestore.collection("tasks").document(taskId)

            try {
                val updateData = mapOf(
                    "title" to updatedTask.title,
                    "description" to updatedTask.description,
                    "status" to updatedTask.status
                )
                taskRef.update(updateData).await()

                val updatedTasks = _tasks.value.map { task ->
                    if (task.id == taskId) {
                        updatedTask
                    } else {
                        task
                    }
                }
                _tasks.value = updatedTasks
            } catch (e: Exception) {
                println("Error updating task with ID: $taskId")
            }
        }
    }


    fun updateTaskStatusInViewModel(taskId: String, newStatus: String) {
        viewModelScope.launch {
            updateTaskStatus(taskId, newStatus)
        }
    }



    fun addTask(newTask: Task) {
        viewModelScope.launch {
            val taskDocument = firestore.collection("tasks").add(newTask).await()
            val updatedTask = newTask.copy(id = taskDocument.id)
            _tasks.value = _tasks.value + updatedTask
        }
    }



    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val taskRef = firestore.collection("tasks").document(taskId)

            try {
                taskRef.delete().await()

                val updatedTasks = _tasks.value.filter { it.id != taskId }
                _tasks.value = updatedTasks
            } catch (e: Exception) {
                println("Error deleting task with ID: $taskId")
            }
        }
    }


    private fun loadTasks() {
        viewModelScope.launch {
            val fetchedTasks = fetchTasks()
            _tasks.value = fetchedTasks
        }
    }
}