package com.example.fitsync.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitsync.kanban.data.Task
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
        return tasksSnapshot.documents.mapNotNull { it.toObject(Task::class.java) }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            val taskRef = firestore.collection("tasks").document(taskId)

            try {
                val taskSnapshot = taskRef.get().await()

                if (taskSnapshot.exists()) {
                    // 해당 문서가 존재하는 경우에만 업데이트 진행
                    val updateData = mapOf(
                        "status" to newStatus
                    )
                    taskRef.update(updateData).await()
                    println("Status updated: $newStatus")

                    val fetchedTasks = fetchTasks() // Firestore로부터 업데이트된 목록 가져옴
                    _tasks.value = fetchedTasks // 로컬 상태를 업데이트하여 UI 업데이트
                } else {
                    println("Task with ID $taskId does not exist.")
                }
            } catch (e: Exception) {
                println("Error updating status of task with ID: $taskId")
            }
        }
    }

    fun addTask(newTask: Task) {
        viewModelScope.launch {
            firestore.collection("tasks").add(newTask).await()
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val fetchedTasks = fetchTasks()
            _tasks.value = fetchedTasks
        }
    }
}
