package com.example.fitsync

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.graphics.Bitmap


class AttendanceActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                var resultText by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    GenerateQRCodeScreen()
                    Spacer(modifier = Modifier.height(16.dp))
                    WriteFirestoreButton(
                        title = "출근",
                        onWrite = {
                            onQRScanComplete("출근")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ReadFirestoreButton(
                        title = "출근 시간 확인",
                        type = "출근",
                        onRead = { timestamp ->
                            val timeText = if (timestamp != null) {
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp)
                            } else {
                                "출근 시간이 없습니다."
                            }
                            resultText = "출근 시간: $timeText"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WriteFirestoreButton(
                        title = "퇴근",
                        onWrite = {
                            onQRScanComplete("퇴근")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ReadFirestoreButton(
                        title = "퇴근 시간 확인",
                        type = "퇴근",
                        onRead = { timestamp ->
                            val timeText = if (timestamp != null) {
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp)
                            } else {
                                "퇴근 시간이 없습니다."
                            }
                            resultText = "퇴근 시간: $timeText"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
    Button(
        modifier = modifier,
        onClick = {
            onWrite()
        }
    ) {
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
    Button(
        modifier = modifier,
        onClick = {
            onRead(type) { timestamp ->
                onRead(timestamp)
            }
        }
    ) {
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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GenerateQRCodeScreen() {
    val memberId = "user123" // 회원 식별 정보 (예시)
    val qrCodeType = "출근" // QR 코드 타입 정보 (출근 또는 퇴근)
    val timestamp = "2023-08-30 15:30:00" // 타임스탬프 정보 (예시)
    val qrData = "$memberId,$qrCodeType,$timestamp"

    val qrCodeBitmap = generateQRCode(qrData)

    var isQRCodeGenerated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        val density = LocalDensity.current.density
        val qrImageBitmap = ImageBitmap(qrCodeBitmap.width, qrCodeBitmap.height) {
            // 생성된 QR 코드 Bitmap의 픽셀 데이터로 ImageBitmap 채우기
            val pixels = IntArray(qrCodeBitmap.width * qrCodeBitmap.height)
            qrCodeBitmap.getPixels(pixels, 0, qrCodeBitmap.width, 0, 0, qrCodeBitmap.width, qrCodeBitmap.height)
            setPixels(pixels)
        }

        // AndroidView로 ImageView를 만들어 ImageBitmap 설정하기
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { context ->
                androidx.appcompat.widget.AppCompatImageView(context).apply {
                    setImageBitmap(qrImageBitmap)
                }
            },
            modifier = Modifier.padding((16 * density).dp)
        )


        Button(
            onClick = {
                isQRCodeGenerated = true
                keyboardController?.hide()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Generate QR Code", fontWeight = FontWeight.Bold)
        }
    }

    if (isQRCodeGenerated) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: ""
        val timeType = qrCodeType
        val currentTime = getCurrentTime()

        val data = hashMapOf(
            "uid" to uid,
            "timeType" to timeType,
            "time" to currentTime
        )

        db.collection("test")
            .add(data)
            .addOnSuccessListener { documentReference ->
                // 성공적으로 Firestore에 저장되었을 때의 처리
            }
            .addOnFailureListener { e ->
                // 저장 실패 시의 처리
            }
    }
}

private fun generateQRCode(data: String): ImageBitmap {
    val width = 500
    val height = 500

    val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        data, BarcodeFormat.QR_CODE, width, height
    )

    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }

    return ImageBitmap(width, height, pixels)
}

private fun getCurrentTime(): String {
    val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentTime)
}