package com.example.fitsync.kanban

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp

class KanbanActivity : ComponentActivity() {
    private val viewModel: KanbanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        setContent {
            KanbanBoardScreen(viewModel)
        }
    }
}