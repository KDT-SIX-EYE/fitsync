package com.example.fitsync

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){
    var enteredText: String = ""
    var selectedColor: Color = Color.White
}