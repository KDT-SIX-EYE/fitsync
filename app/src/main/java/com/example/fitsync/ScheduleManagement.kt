package com.example.fitsync

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class ScheduleManagement : ComponentActivity() {
    private val calenderViewModel: CalenderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val db = Firebase.firestore
            asd(db, calenderViewModel)
        }
    }
}

//data class SharedData(val clickedDate: Int)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun asd(db: FirebaseFirestore, calenderViewModel: CalenderViewModel) {
    Column {
        val context = LocalContext.current
        var memberName by remember {
            mutableStateOf("")
        }
        var trainerName by remember {
            mutableStateOf("")
        }
        val activity = LocalContext.current as? Activity
        val sharedPref = remember {
            activity?.getPreferences(Context.MODE_PRIVATE)
        }


        // sharedPref에서 clickedDate 값을 읽어옵니다.
//        val clickedDate = sharedPref?.getInt("clickedDate", 0) ?: 0
        TextField(value = memberName, onValueChange = {memberName = it})
        TextField(value = trainerName, onValueChange = {trainerName = it})
        Button(
            onClick = {
                val userData = hashMapOf(
                    "Member Name" to memberName,
                    "Trainer Name" to trainerName,
                    "Clicked Date" to calenderViewModel.clickedDateState.value
//                    "Clicked Date" to clickedDate
                )
                db.collection("schedule").add(userData)
            }
        )
        {  }
//        val calenderViewModel = remember {
//            ViewModelProvider(activity)[CalenderViewModel::class.java]
//        }
        Text(text = "Submit")
    }
}