package com.example.fitsync

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.fitsync.ui.theme.FitSyncTheme

class CalenderActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            FitSyncTheme {
                CalenderScreen()
            }
        }
    }
}

@Composable
fun CalenderScreen() {
    Text(text = "캘린더 화면입니다")
}
