package com.example.fitsync

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue

class ScheduleManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            ScheduleManagementScreen(db)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleManagementScreen(db: FirebaseFirestore) {
    var calendarOpen by remember {
        mutableStateOf(false)
    }
    var clickedDate by remember {
        mutableStateOf(0)
    }
    var timeListOpen by remember {
        mutableStateOf(false)
    }
    val startTime = 7
    val endTime = 23
    val timeOptions = mutableListOf<Int>()

    for (hour in startTime until endTime) {
        timeOptions.add(hour)
    }
    var selectedTime by remember {
        mutableStateOf(0) // 기본 시간을 선택합니다.
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp), // Center align the contents
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var memberName by remember {
            mutableStateOf("")
        }

        Button(
            onClick = { calendarOpen = true },
            colors = ButtonDefaults.buttonColors(Color.Black),
            modifier = Modifier.width(180.dp)
        ) {
            Text(text = "날짜 $clickedDate")
        }
        Button(
            onClick = { timeListOpen = true },
            colors = ButtonDefaults.buttonColors(Color.Black),
            modifier = Modifier.width(180.dp)
        ) {
            Text(text = "시간 $selectedTime")
        }



        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var trainerList by remember { mutableStateOf(listOf<String>()) }
            var memberList by remember { mutableStateOf(listOf<String>()) }
//                TextField(value = memberName, onValueChange = { memberName = it })
            var members = mutableListOf<String>()
            db.collection("users").get().addOnSuccessListener { field ->
                for (memberName in field) {
                    var member = memberName.get("name").toString()
                    members.add(member)
                }
                memberList = members
            }
            var trainers = mutableListOf<String>()
            db.collection("trainer").get().addOnSuccessListener { field ->
                for (trainerName in field) {
                    var trainer = trainerName.get("name").toString()
                    trainers.add(trainer)
                }
                trainerList = trainers
            }
            var expandedTrainerIndex by remember { mutableStateOf(-1) }
            var selectedTrainer by remember { mutableStateOf("") }
            var searchQuery by remember { mutableStateOf(TextFieldValue()) }
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery.text,
                    onValueChange = { newValue ->
                        searchQuery = TextFieldValue(newValue)
                    },
                    placeholder = { Text("Search for a trainer") },
                    modifier = Modifier
                        .width(200.dp),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                )
                Spacer(modifier = Modifier.height(16.dp))

                val filteredTrainers =
                    trainerList.filter { it.contains(searchQuery.text, ignoreCase = true) }

                if (searchQuery.text.isNotBlank()) {
                    for (trainer in filteredTrainers) {
                        Text(
                            text = trainer,
                            modifier = Modifier.clickable {
                                selectedTrainer = trainer
                            }
                        )
                    }
                } else {
                    Text("Enter a search query to see the list.")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedTrainer.isNotBlank()) {
                    Text("Selected Trainer: $selectedTrainer")
                }
            }
            var searchQuery2 by remember { mutableStateOf(TextFieldValue()) }
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery2.text,
                    onValueChange = { newValue ->
                        searchQuery2 = TextFieldValue(newValue)
                    },
                    placeholder = { Text("Search for a member") },
                    modifier = Modifier
                        .width(200.dp),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filtered trainer list based on search query
                val filteredMembers =
                    memberList.filter { it.contains(searchQuery2.text, ignoreCase = true) }

                // Display the filtered trainers only if search query is not empty
                if (searchQuery2.text.isNotBlank()) {
                    for (member in filteredMembers) {
                        Text(
                            text = member,
                            modifier = Modifier.clickable {
                                // Set the selected trainer when clicked
                                memberName = member
                            }
                        )
                    }
                } else {
                    Text("Enter a search query to see the list.")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display the selected trainer
                if (memberName.isNotBlank()) {
                    Text("Selected Member: $memberName")
                }
            }

            if (expandedTrainerIndex != -1) {
                trainerList.forEachIndexed { index, trainer ->
                    Text(
                        text = trainer,
                        modifier = Modifier
                            .clickable {
                                selectedTrainer = trainer
                                expandedTrainerIndex = -1
                            }
                    )
                }
            }
            Button(
                onClick = {
                    val userData = hashMapOf(
                        "Member Name" to memberName,
                        "Trainer Name" to selectedTrainer,
                        "Clicked Date" to clickedDate,
                        "Selected Time" to selectedTime
                    )
                    val baseDocumentRef =
                        db.collection("schedule").document("$clickedDate")
//                                .collection("Time").document("$selectedTime")

                    fun findAvailableDocumentName(
                        documentRef: DocumentReference,
                        candidateName: String,
                        attempt: Int = 1,
                        maxAttempts: Int = 3
                    ) {
                        val newDocumentCandidate =
                            if (attempt == 1) candidateName else "${candidateName}_$attempt"
                        documentRef.collection("Time").document(newDocumentCandidate).get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val documentSnapshot = task.result
                                    if (!documentSnapshot.exists()) {
                                        val newDocumentRef = documentRef.collection("Time")
                                            .document(newDocumentCandidate)
                                        newDocumentRef.set(userData)
                                    } else {
                                        if (attempt < maxAttempts) {
                                            findAvailableDocumentName(
                                                documentRef,
                                                candidateName,
                                                attempt + 1,
                                                maxAttempts
                                            )
                                        } else {
                                            Log.w(
                                                TAG,
                                                "Maximum additional documents reached for this time."
                                            )
                                        }
                                    }
                                } else {
                                    Log.w(
                                        TAG,
                                        "Error checking document existence: ",
                                        task.exception
                                    )
                                }
                            }
                    }

                    findAvailableDocumentName(baseDocumentRef, "$selectedTime")

                }, colors = ButtonDefaults.buttonColors(Color.Black)
            )
            { Text(text = "예약") }
        }

    }
    if (calendarOpen) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 250.dp), // Center align the contents
        ) {
            val dataSource = CalendarDataSource()
            var calendarUiModel by remember {
                mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
            }

            fun convertLocalDateToInt(dateModel: CalendarUiModel.Date): Int {
                val date = dateModel.date
                return date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
            }
            CalendarWindow(data = calendarUiModel, onClickedDate = { calendarOpen = false },
                onPrevClickListener = { startDate ->
                    val finalStartDate = startDate.minusDays(1)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel.selectedDate.date
                    )
                }, onNextClickListener = { endDate ->
                    val finalStartDate = endDate.plusDays(2)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel.selectedDate.date
                    )
                }, onDateClickListener = { date ->
                    calendarUiModel = calendarUiModel.copy(
                        selectedDate = date,
                        visibleDates = calendarUiModel.visibleDates.map {
                            it.copy(
                                isSelected = it.date.isEqual(date.date)
                            )
                        }
                    )
                    clickedDate = convertLocalDateToInt(calendarUiModel.selectedDate)

                }
            )
        }

    }

    if (timeListOpen) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 300.dp)
                .height(250.dp)
                .width(150.dp)
                .background(Color.White)
        ) {
            item {
                timeOptions.forEach { timeOption ->
                    Text(
                        text = "$timeOption ~ ${timeOption + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTime = timeOption
                                timeListOpen = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarWindow(
    data: CalendarUiModel,
    onClickedDate: () -> Unit,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onDateClickListener: (CalendarUiModel.Date) -> Unit
) {
    val context = LocalContext.current
    FirebaseApp.initializeApp(context)
    Column(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp)
            .background(Color.White)
    ) {
        var currentYearMonth by remember {
            mutableStateOf(YearMonth.now())
        }
        Header(
            data = data,
            onPrevClickListener = {
                onPrevClickListener(data.startDate.date)
            },
            onNextClickListener = {
                onNextClickListener(data.endDate.date)
            },
            onMinusMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
            onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) }
        )
        Content(
            currentYearMonth = currentYearMonth, data = data,
            onDateClickListener =
            onDateClickListener,
            onClickedDate = onClickedDate
        )
    }
}


@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onMinusMonth: () -> Unit,
    onPlusMonth: () -> Unit
) {
    val currentYearMonth = YearMonth.now()
    val currentMonth = remember { mutableStateOf(currentYearMonth) }
    Column(modifier = Modifier.padding(10.dp)) {
        Row {
            Text(
                text =
                currentMonth.value.format(
                    DateTimeFormatter.ofPattern(
                        "yyyy년 MMMM",
                        Locale("ko")
                    )
                ),
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )
            IconButton(onClick = {
                onPrevClickListener(data.startDate.date)
                val newMonth = currentMonth.value.minusMonths(1)
                currentMonth.value = newMonth
                onMinusMonth()
            }) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Back"
                )
            }
            IconButton(onClick = {
                onNextClickListener(data.endDate.date)
                val newMonth = currentMonth.value.plusMonths(1)
                currentMonth.value = newMonth
                onPlusMonth()
            }) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next"
                )
            }
        }
    }
}

@Composable
fun Content(
    currentYearMonth: YearMonth,
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    onClickedDate: () -> Unit
) {
    Column {
        repeat(6) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val startIndex = rowIndex * 7
                val endIndex = minOf(startIndex + 7, data.visibleDates.size)
                for (i in startIndex until endIndex) {
                    val date = data.visibleDates[i]
                    ContentItem(
                        currentYearMonth = currentYearMonth,
                        date = date,
                        onDateClickListener,
                        onClickedDate = onClickedDate
                    )
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    currentYearMonth: YearMonth,
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
    onClickedDate: () -> Unit
) {
    val textColor =
        if (date.isSelected) {
            Color.White
        } else if (date.date.year == currentYearMonth.year && date.date.month == currentYearMonth.month) {
            if (date.date.dayOfWeek == DayOfWeek.SUNDAY) {
                Color.Black
            } else if (date.date.dayOfWeek == DayOfWeek.SATURDAY) {
                Color.Black
            } else {
                Color.Black
            }
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        }
    val textStyle =
        if (date.date.dayOfWeek == DayOfWeek.SUNDAY || date.date.dayOfWeek == DayOfWeek.SATURDAY) {
            MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black)
        } else {
            MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
        }
    val backgroundColor = if (date.isToday && !date.isSelected) {
        Color.LightGray
    } else if (date.isSelected) {
        Color.Black
    } else {
        Color.White
    }
    var lastClickTime by remember {
        mutableStateOf(0L)
    }
    var clickCount by remember { mutableStateOf(0) }


    Card(
        modifier = Modifier
            .width(30.dp)
            .height(30.dp)
            .clickable {
                clickCount++
                val currentTime = System.currentTimeMillis()

                lastClickTime = currentTime
                onClickListener(date)
                onClickedDate()

            },
        colors = CardDefaults.cardColors(
            backgroundColor
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = textStyle,
                color = textColor,
                fontSize = 10.sp
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = textStyle,
                color = textColor,
                fontSize = 10.sp
            )
        }
    }
}