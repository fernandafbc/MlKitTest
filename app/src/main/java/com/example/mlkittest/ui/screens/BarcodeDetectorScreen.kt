package com.example.mlkittest.ui.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mlkittest.ui.analyzers.BarcodeAnalyzer

@Composable
fun BarcodeDetectorScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var barcodeResult by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AndroidView(
                modifier = Modifier.size(250.dp),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).also {
                        it.scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val executor = ContextCompat.getMainExecutor(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        val imageCapture = ImageCapture.Builder().build()

                        val imageAnalysis = ImageAnalysis.Builder()
                            .build()
                            .apply {
                                setAnalyzer(executor, BarcodeAnalyzer(
                                    callback = { barcode ->
                                        barcodeResult = barcode.rawValue ?: ""
                                    }
                                ))
                            }

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalysis
                        )
                    }, executor)
                    previewView
                },
            )
            Text(
                text = "Código de barras: $barcodeResult",
                style = TextStyle(color = Color.Red, fontSize = 18.sp),
                modifier = Modifier.padding(48.dp)
            )
        }
    }
}