package com.example.fitsync

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.Locale


class AttendanceActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                var resultText by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    generateBitmapQRCode("안녕하세요")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val intent = Intent(this@AttendanceActivity, QRcheckActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text(text = "QR스캔")
                    }
                        Spacer(modifier = Modifier.height(16.dp))
                        WriteFirestoreButton(title = "출근", onWrite = {
                            onQRScanComplete("출근")
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                        ReadFirestoreButton(
                            title = "출근 시간 확인", type = "출근", onRead = { timestamp ->
                                val timeText = if (timestamp != null) {
                                    SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                                    ).format(timestamp)
                                } else {
                                    "출근 시간이 없습니다."
                                }
                                resultText = "출근 시간: $timeText"
                            }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        WriteFirestoreButton(title = "퇴근", onWrite = {
                            onQRScanComplete("퇴근")
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                        ReadFirestoreButton(
                            title = "퇴근 시간 확인", type = "퇴근", onRead = { timestamp ->
                                val timeText = if (timestamp != null) {
                                    SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                                    ).format(timestamp)
                                } else {
                                    "퇴근 시간이 없습니다."
                                }
                                resultText = "퇴근 시간: $timeText"
                            }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = resultText)
                        Spacer(modifier = Modifier.height(16.dp))


                    }
                }
            }
        }
    }


    @Composable
    fun WriteFirestoreButton(
        title: String,
        onWrite: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Button(modifier = modifier, onClick = {
            onWrite()
        }) {
            Text(title)
        }
    }

    @Composable
    fun ReadFirestoreButton(
        title: String,
        type: String,
        onRead: (Long?) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Button(modifier = modifier, onClick = {
            onRead(type) { timestamp ->
                onRead(timestamp)
            }
        }) {
            Text(title)
        }
    }

    fun onWrite(type: String, timestamp: Long) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("attendance").document(type)
        docRef.set(
            mapOf(
                "timestamp" to timestamp
            )
        )
    }

    fun onRead(type: String, onResult: (Long?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("attendance").document(type)
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val timestamp = document.getLong("timestamp")
                onResult(timestamp)
            } else {
                onResult(null)
            }
        }
    }

    fun onQRScanComplete(type: String) {
        val currentTime = System.currentTimeMillis()
        onWrite(type, currentTime)
    }

    /** QRCode Bitmap 생성 */
    private fun generateBitmapQRCode(contents: String): Bitmap {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(contents, BarcodeFormat.QR_CODE, 512, 512)
    }
