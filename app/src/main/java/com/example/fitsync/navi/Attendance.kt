package com.example.fitsync.navi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitsync.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Attendance(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar",
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ScreenRoute.Main_Kanban.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
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
        }
    ) { innerPadding ->

        AttendanceScreen(navController = navController)

    }

    val onBack: () -> Unit = {

        navController.navigate(ScreenRoute.Main_Kanban.route)
    }
    BackHandler {
        onBack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController) {
    val db = Firebase.firestore
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
//        val receivedManagerName = intent.getStringExtra("managerName")
//        val managerName = remember { mutableStateOf(receivedManagerName ?: "") }

        val basicDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
        val currentDate = basicDate.format(dateFormatter)

//        TextField(
//            value = managerName.value, onValueChange = { newValue ->
//                managerName.value = newValue
//            },
//            placeholder = { Text("이름") },
//            singleLine = true,
//            modifier = Modifier
//                .fillMaxWidth(0.45f)
//                .align(Alignment.CenterHorizontally)
//                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
//            colors = TextFieldDefaults.textFieldColors(
//                textColor = Color.Black,
//                containerColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent
//            ),
//            textStyle = androidx.compose.ui.text.TextStyle(
//                color = Color.Black, textAlign = TextAlign.Center, fontSize = 20.sp
//            )
//        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
//                        if (managerName.isNotBlank()) { // 또는 if (managerName != null && managerName.isNotEmpty())
                navController.navigate(ScreenRoute.QR.route)

            },
//            enabled = managerName.value.isNotBlank(),
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(text = "QR스캔")
        }
        var startEndInfo by remember {
            mutableStateOf(false)
        }
        var startWork by remember {
            mutableStateOf("")
        }
        var endWork by remember {
            mutableStateOf("")
        }

//        db.collection("Attendence")
//            .document(basicDate.toString())
//            .collection("ManagerName")
//            .get()
//            .addOnSuccessListener { results ->
//                for (result in results) {
//                    val documentManagerName = result.id
//                    if (documentManagerName == managerName.value) {
//                        val getStartWork = result.getString("ATTENDENCE") ?: "-"
//                        val getEndWork = result.getString("FINISH") ?: "-"
//                        startWork = getStartWork
//                        endWork = getEndWork
//                        break
//                    }
//                }
//            }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$currentDate")
            Spacer(modifier = Modifier.height(5.dp))
            Text("출근 : $startWork")
            Text("퇴근 : $endWork")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { startEndInfo = !startEndInfo },
            colors = ButtonDefaults.buttonColors(
                Color.Black
            )
        ) {
            Text("출퇴근 직원 확인")
        }
        if (startEndInfo) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    var managerNameList by remember { mutableStateOf(listOf<String>()) }
                    var attendenceInfoList by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
                    val tempList = mutableListOf<String>()
                    val attendenceInfo = mutableListOf<Triple<String, String, String>>()
                    db.collection("Attendence")
                        .document(basicDate.toString())
                        .collection("ManagerName")
                        .get().addOnSuccessListener { documents ->
                            for (document in documents) {
                                val managername = document.id
                                val startWorkHistory =
                                    document.get("ATTENDENCE").toString()
                                val endWorkHistory = document.get("FINISH").toString()
                                tempList.add(managername)
                                attendenceInfo.add(
                                    Triple(
                                        managername,
                                        startWorkHistory,
                                        endWorkHistory
                                    )
                                )
                            }
                            managerNameList = tempList
                            attendenceInfoList = attendenceInfo
                        }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("날짜 : $currentDate")
                    Spacer(modifier = Modifier.height(4.dp))
                    for (info in attendenceInfoList) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(8.dp)
                                .background(Color.White)
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("이름 : ${info.first}", color = Color.Black)
                                Text("출근 : ${info.second}", color = Color.Black)
                                Text("퇴근 : ${info.third}", color = Color.Black)
                            }
                        }

                    }
                }
            }

        }
    }
}