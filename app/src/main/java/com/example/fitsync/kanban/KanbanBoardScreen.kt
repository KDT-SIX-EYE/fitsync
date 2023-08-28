package com.example.fitsync.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.fitsync.kanban.data.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardScreen(viewModel: KanbanViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    val db = Firebase.firestore

    @Composable
    fun AddTaskForm(onTaskAdded: (Task) -> Unit) {
        var title by remember { mutableStateOf(TextFieldValue()) }
        var description by remember { mutableStateOf(TextFieldValue()) }

        Column {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Button(
                onClick = {
                    val newTask = Task(
                        id = UUID.randomUUID().toString(),
                        title = title.text,
                        description = description.text,
                        status = "To Do"
                    )
                    onTaskAdded(newTask)
                    title = TextFieldValue("")
                    description = TextFieldValue("")
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text("Add Task")
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                StatusColumn("To Do", tasks.filter { it.status == "To Do" }) { task, newStatus ->
                    viewModel.updateTaskStatus(task.id, newStatus)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                StatusColumn("In Progress", tasks.filter { it.status == "In Progress" }) { task, newStatus ->
                    viewModel.updateTaskStatus(task.id, newStatus)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            StatusColumn("Done", tasks.filter { it.status == "Done" }) { task, newStatus ->
                viewModel.updateTaskStatus(task.id, newStatus)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            AddTaskForm { newTask ->
                viewModel.addTask(newTask)
            }
        }
    }
}

@Composable
fun StatusColumn(status: String,
                 tasks: List<Task>,
                 onTaskStatusChanged: (Task, String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)
    ) {
        Text(text = status, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.height(8.dp))

        tasks.forEach { task ->
            TaskCard(task, onTaskStatusChanged)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskCard(task: Task, onTaskStatusChanged: (Task, String) -> Unit) {
    val currentStatus = remember { mutableStateOf(task.status) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        Text(text = task.title, fontWeight = FontWeight.Bold)
        Text(text = task.description)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val newStatus = when (task.status) {
                    "To Do" -> "In Progress"
                    "In Progress" -> "Done"
                    else -> task.status
                }

                onTaskStatusChanged(task, newStatus)
                currentStatus.value = newStatus
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text(if (task.status == "To Do") "Start" else "Complete")
        }

        // LaunchedEffect를 사용하여 상태가 변경되었을 때 UI 업데이트
        LaunchedEffect(currentStatus.value) {
            // 상태 변경에 따른 UI 업데이트 로직 작성 (필요하면)
        }
    }
}