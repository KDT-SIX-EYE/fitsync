package com.example.fitsync
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Size
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.LocalTime


class QRcheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                QRScreen()
            }
        }
    }

    @Composable
    private fun QRScreen() {
        val db = Firebase.firestore
        val code by remember {
            mutableStateOf("")
        }
        var hasReadCode by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFeature = remember {
            ProcessCameraProvider.getInstance(context)
        }
        var hasCamPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasCamPermission = granted
            })
        LaunchedEffect(key1 = true) {
            launcher.launch(Manifest.permission.CAMERA)
        }
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (hasCamPermission) {
                if (hasReadCode) {
                    LoadWebUrl(code)
                    BackHandler {
                        restartApp()
                    }
                } else {
                    AndroidView(
                        factory = { context ->
                            val previewView = PreviewView(context)
                            val preview = Preview.Builder().build()
                            val selector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(
                                Size(
                                    previewView.width, previewView.height
                                )
                            ).setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST).build()
                            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),
                                QRCodeAnalyzer {
                                    if (!hasReadCode) {
                                        hasReadCode = true
                                        val receivedManagerName = intent.getStringExtra("managerName")
                                        val currentDate = LocalDate.now()
                                        val currentTime = LocalTime.now()
                                        val hour = currentTime.hour
                                        val minute = currentTime.minute
                                        val second = currentTime.second
                                        val managerDocumentRef = receivedManagerName?.let {
                                            db.collection("Attendence").document(currentDate.toString())
                                                .collection("ManagerName").document(it)
                                        }
                                        managerDocumentRef!!.get().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val document = task.result
                                                val attendenceValue = document.getString("ATTENDENCE")
                                                val finishValue = document.getString("FINISH")

                                                if (attendenceValue == null) {
                                                    val attendenceData = hashMapOf(
                                                        "ATTENDENCE" to "$hour:$minute:$second"
                                                    )
                                                    managerDocumentRef.set(attendenceData)
                                                } else if (finishValue == null) {
                                                    val finishWorkTime = hashMapOf(
                                                        "FINISH" to "$hour:$minute:$second"
                                                    )
                                                    managerDocumentRef.set(
                                                        finishWorkTime, SetOptions.merge()
                                                    )
                                                }

                                                val intent =
                                                    Intent(context, AttendanceActivity::class.java)
                                                intent.putExtra(
                                                    "qrScanType", if (finishValue != null) "퇴근" else "출근"
                                                )
                                                intent.putExtra("managerName", receivedManagerName)
                                                context.startActivity(intent)
                                            }
                                        }
                                    }
                                })
                            try {
                                val cameraProvider = cameraProviderFeature.get()
                                cameraProvider.unbindAll() // Release any previous camera sessions
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner, selector, preview, imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            previewView
                        }, modifier = Modifier.border(5.dp, color = Color.White.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }

    @Composable
    fun LoadWebUrl(url: String) {
        AndroidView(factory = {
            WebView(this).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        })
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finishAffinity()
    }

}


class QRCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {


    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )


    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            val bytes = image.planes.first().buffer.toByteArray()

            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBmp)
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}