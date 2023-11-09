package com.example.fitsync.navi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.util.Size
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.fitsync.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

//@Composable
//fun QR(navController: NavController) {
//    QRScreen(navController)
//
//}

@Composable
fun QR(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = firebaseAuth.currentUser
    val db = Firebase.firestore
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
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasCamPermission = granted
            })
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(
                    Size(
                        previewView.width, previewView.height
                    )
                ).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),
                    QRCodeAnalyzer {
                        if (!hasReadCode) {
                            hasReadCode = true
                            val currentDate = LocalDate.now()
                            val currentTime = LocalTime.now()
                            val hour = currentTime.hour
                            val minute = currentTime.minute
                            val second = currentTime.second
                            val managerDocumentRef = user?.let {
                                user?.displayName?.let { it1 ->
                                    db.collection("Attendence")
                                        .document(currentDate.toString())
                                        .collection("ManagerName").document(it1)
                                }
                            }
                            managerDocumentRef!!.get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    val attendenceValue =
                                        document.getString("ATTENDENCE")
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

                                    navController.navigate(ScreenRoute.Attendance.route)
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


//@Composable
//fun LoadWebUrl(url: String) {
//    AndroidView(factory = {
//        WebView(this).apply {
//            webViewClient = WebViewClient()
//            loadUrl(url)
//        }
//    })
//}

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