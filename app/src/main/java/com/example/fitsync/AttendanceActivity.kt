package com.example.fitsync

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.text.Text
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class AttendanceActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {

            }
        }
    }
}


class CheckScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                // Your existing code

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff40407a)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Your existing code

                    if (hasCamPermission) {
                        if (hasReadCode) {
                            LoadWebUrl(code)
                            BackHandler {
                                restartApp()
                            }
                        } else {
                            // Your existing camera setup

                            Button(
                                onClick = {
                                    // Generate QR code and save data to Firestore
                                    val uid = "user_uid"  // Replace with actual user UID
                                    val currentDate = "2023-08-28"  // Replace with actual date
                                    val currentTime = "14:30"  // Replace with actual time
                                    val qrData = "$uid:$currentDate:$currentTime"

                                    val writer = QRCodeWriter()
                                    val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
                                    val image = ImageBitmap
                                        .bitmapsFrom(bitMatrix)
                                        .first()

                                    // Display generated QR code
                                    this@CheckScreenActivity.GeneratedQRCode(image)

                                    // Save data to Firestore
                                    val firestore = FirebaseFirestore.getInstance()
                                    val qrCodeData = hashMapOf(
                                        "uid" to uid,
                                        "date" to currentDate,
                                        "time" to currentTime
                                    )

                                    firestore.collection("qr_codes")
                                        .add(qrCodeData)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding document", e)
                                        }
                                },
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Generate QR Code")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun GeneratedQRCode(image: ImageBitmap) {
        // Display the generated QR code
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
    }

    // Your existing functions
}