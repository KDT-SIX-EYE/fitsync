package com.example.fitsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AttendanceActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                val db = Firebase.firestore
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                    val receivedManagerName = intent.getStringExtra("managerName")
                    val managerName = remember { mutableStateOf(receivedManagerName ?: "") }

                    val basicDate = LocalDate.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
                    val currentDate = basicDate.format(dateFormatter)

                    TextField(value = managerName.value, onValueChange = { newValue ->
                        managerName.value = newValue
                    },
                        placeholder = {Text("이름")},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .align(Alignment.CenterHorizontally)
                            .border(0.5.dp, Color.DarkGray , RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black, textAlign = TextAlign.Center, fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
//                        if (managerName.isNotBlank()) { // 또는 if (managerName != null && managerName.isNotEmpty())
                        val intent = Intent(this@AttendanceActivity, QRcheckActivity::class.java)
                        intent.putExtra("managerName", managerName.value)
                        startActivity(intent)

                    }, enabled = managerName.value.isNotBlank()) {
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

                    db.collection("Attendence")
                        .document(basicDate.toString())
                        .collection("ManagerName")
                        .get()
                        .addOnSuccessListener { results ->
                            for (result in results) {
                                val documentManagerName = result.id
                                if (documentManagerName == managerName.value) {
                                    val getStartWork = result.getString("ATTENDENCE") ?: "-"
                                    val getEndWork = result.getString("FINISH") ?: "-"
                                    startWork = getStartWork
                                    endWork = getEndWork
                                    break
                                }
                            }
                        }
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$currentDate")
                        Text("출근 : $startWork")
                        Text("퇴근 : $endWork")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = { startEndInfo = !startEndInfo }) {
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
                                Text("날짜 : $currentDate")
                                for (info in attendenceInfoList) {
                                    Text("이름 : ${info.first}")
                                    Text("출근 : ${info.second}")
                                    Text("퇴근 : ${info.third}")
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }


            }
        }
    }
}