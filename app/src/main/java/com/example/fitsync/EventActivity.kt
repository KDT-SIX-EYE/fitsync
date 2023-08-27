package com.example.fitsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext

class EventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                val context = LocalContext.current

                Button(onClick = {
                    val intent = Intent(context, EventManagementActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Text(text = "일정 확인")}
            }
        }
    }
}