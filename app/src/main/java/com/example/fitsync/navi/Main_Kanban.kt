package com.example.fitsync.navi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitsync.R
import com.example.fitsync.data.Task
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun Main_Kanban(navController: NavController) {
    val viewModel: Kanban_ViewModel = viewModel()

    MainScreen(navController, viewModel)

    var closeApp by remember {
        mutableStateOf(false)
    }

    // BackHandler 사용
    BackHandler {
        // Kanban 화면일 때만 뒤로가기 처리
        closeApp = true
    }

    if (closeApp) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(200.dp)
                .width(300.dp)
                .background(Color.LightGray.copy(alpha = 0.7f))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // 세로 축을 가운데로 정렬
                verticalArrangement = Arrangement.Center,
            ) {
                Row(modifier = Modifier.padding(start = 20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.system_shutdown_icon),
                        contentDescription = null
                    )
                    Text(
                        text = "정말로 종료하시겠습니까?",
                        fontSize = 25.sp
                    )

                }
                Row(modifier = Modifier.padding(20.dp)) {
                    Button(
                        onClick = { android.os.Process.killProcess(android.os.Process.myPid()) },
                        colors = ButtonDefaults.buttonColors(
                            Color.White.copy(alpha = 0f),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "예",
                            fontSize = 30.sp
                        )
                    }
                    Button(
                        onClick = { closeApp = false },
                        colors = ButtonDefaults.buttonColors(
                            Color.White.copy(alpha = 0f),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "아니요", fontSize = 30.sp)
                    }
                }


            }
        }
        BackHandler(enabled = true) {
            closeApp = false
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: Kanban_ViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fit Sync",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* 메뉴 아이콘 */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "메뉴 아이콘"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(ScreenRoute.MyProfile.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "사용자 프로필"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val context = LocalContext.current

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            navController.navigate(ScreenRoute.Calender.route)
                        }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "캘린더 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "캘린더",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            navController.navigate(ScreenRoute.Main_Kanban.route)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "메인 액티비티(홈)으로 이동"
                            )
                        }
                        Text(
                            text = "Home",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            navController.navigate(ScreenRoute.Attendance.route)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_qr_code_2_24),
                                contentDescription = "QR 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "QR",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
//                            val intent = Intent(context, UsersActivity::class.java)
//                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "사용자 목록 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "프로필",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            navController.navigate(ScreenRoute.Messenger.route)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_mark_chat_unread_24),
                                contentDescription = "메신저 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "채팅방",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                }
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            KanbanBoardScreen(viewModel)
        }
    }
}

@Composable
fun KanbanBoardScreen(viewModel: Kanban_ViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    var isAddTaskDialogVisible by remember { mutableStateOf(false) }
    val showDialog = { isAddTaskDialogVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
        ) {
            item {
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
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(end = 16.dp, bottom = 16.dp)
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
                                        .height(50.dp)
                                        .padding(bottom = 8.dp)
                                ) {
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
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskForm(viewModel: Kanban_ViewModel) {
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
            label = { Text("할 일 (ex : 워크인 상담)", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("설명 (ex : 담당자/pm 2~4)", color = Color.Gray) },
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
                .wrapContentWidth()
                .height(50.dp)
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
    viewModel: Kanban_ViewModel
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .padding(8.dp)
            .background(Color.Transparent)
    ) {
        Text(
            text = status,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    viewModel: Kanban_ViewModel,
    onTaskDeleted: (Task) -> Unit
) {
    var isEditDialogVisible by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var status by remember { mutableStateOf(task.status) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { isEditDialogVisible = true }, // 클릭 시 수정 다이얼로그 표시
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp
            )
            Text(
                text = task.description,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
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

                        // Use viewModelScope.launch to run the suspend function in a coroutine
                        viewModel.updateTaskStatusInViewModel(task.id, newStatus)
                    },
                    colors = ButtonDefaults.buttonColors(Color.Black),
                    modifier = Modifier
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

        // 수정 다이얼로그 표시
        if (isEditDialogVisible) {
            AlertDialog(
                onDismissRequest = { isEditDialogVisible = false },
                title = { Text("작업 수정", color = Color.Black) },
                confirmButton = {
                    Button(
                        onClick = {
                            val updatedTask = task.copy(
                                title = title,
                                description = description,
                                status = status
                            )
                            viewModel.updateTask(task.id, updatedTask)

                            isEditDialogVisible = false
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Text("수정", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isEditDialogVisible = false },
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Text("취소", color = Color.White)
                    }
                },
                text = {
                    Column {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("제목") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("설명") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            )
        }
    }
}
