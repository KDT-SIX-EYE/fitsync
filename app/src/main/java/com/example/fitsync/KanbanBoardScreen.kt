package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.example.fitsync.ViewModel.KanbanViewModel
import com.example.fitsync.data.Task
import java.util.UUID

class KanbanBoardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

@Composable
fun KanbanBoardScreen(viewModel: KanbanViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    var isAddTaskDialogVisible by remember { mutableStateOf(false) }
    val showDialog = { isAddTaskDialogVisible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            StatusColumn(
                status = "해야 할 일",
                tasks = tasks.filter { it.status == "To Do" },
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatusColumn(
                status = "진행 중",
                tasks = tasks.filter { it.status == "In Progress" },
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatusColumn(
                status = "완료",
                tasks = tasks.filter { it.status == "Done" },
                viewModel = viewModel
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            IconButton(
                onClick = showDialog,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            if (isAddTaskDialogVisible) {
                AlertDialog(
                    onDismissRequest = { isAddTaskDialogVisible = false },
                    title = { Text("할 일 추가", color = Color.Black) },
                    confirmButton = { /* 비어있음 */ },
                    dismissButton = {
                        Button(
                            onClick = { isAddTaskDialogVisible = false },
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(bottom = 8.dp)                        ) {
                            Text("닫기", color = Color.White)
                        }
                    },
                    text = {
                        AddTaskForm(viewModel = viewModel)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskForm(viewModel: KanbanViewModel) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("할 일", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("설명", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Button(
            onClick = {
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    title = title.text,
                    description = description.text,
                    status = "To Do"
                )
                viewModel.addTask(newTask)
                title = TextFieldValue("")
                description = TextFieldValue("")
            },
            colors = ButtonDefaults.buttonColors(Color.Black),
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 8.dp)
        ) {
            Text("할 일 추가", color = Color.White)
        }
    }
}

@Composable
fun StatusColumn(
    status: String,
    tasks: List<Task>,
    viewModel: KanbanViewModel
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .padding(8.dp)
            .background(Color.Black)
    ) {
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            tasks.forEach { task ->
                TaskCard(
                    task = task,
                    viewModel = viewModel,
                    onTaskDeleted = { deletedTask ->
                        viewModel.deleteTask(deletedTask.id)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    viewModel: KanbanViewModel,
    onTaskDeleted: (Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { /* Handle card click if needed */ }
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
//        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = task.description,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val newStatus = when (task.status) {
                        "To Do" -> "In Progress"
                        "In Progress" -> "Done"
                        "Done" -> {
                            onTaskDeleted(task)
                            return@Button
                        }
                        else -> task.status
                    }
                    viewModel.updateTaskStatus(task.id, newStatus)
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .align(Alignment.End)
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = when (task.status) {
                        "To Do" -> "Start"
                        "In Progress" -> "Complete"
                        "Done" -> "Delete"
                        else -> ""
                    },
                    color = Color.White
                )
            }
        }
    }
}