package com.example.fitsync

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fitsync.ui.theme.FitSyncTheme

class ScheduleActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            FitSyncTheme {
var a = 1
            }
        }
    }
}
