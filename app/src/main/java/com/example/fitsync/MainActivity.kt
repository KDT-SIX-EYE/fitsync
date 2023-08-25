package com.example.fitsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.fitsync.ui.theme.FitSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        val context = LocalContext.current
                        Button(onClick = {
                            val intent = Intent(context, MainActivity2::class.java)
                            context.startActivity(intent)
                        }) {
                            Text(text = "캘린더")
                        }
                        Button(onClick = {
                            val intent = Intent(context, ScheduleActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Text(text = "스케줄")
                        }
                        Button(onClick = {
                            val intent = Intent(context, BookActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Text(text = "예약")
                        }
                        Button(onClick = {
                            val intent = Intent(context, CalenderActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Text(text = "캘린더")
                        }
                    }
                }
            }
        }
    }
}

