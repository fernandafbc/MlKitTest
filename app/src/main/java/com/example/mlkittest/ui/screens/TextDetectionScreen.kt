package com.example.mlkittest.ui.screens

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mlkittest.ui.analyzers.TextAnalyzer

@Composable
fun TextDetectionScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var textResult by rememberSaveable { mutableStateOf("") }
    var language by rememberSaveable { mutableStateOf("") }
    var rect by remember { mutableStateOf<Rect?>(null) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                modifier = Modifier.matchParentSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = ContextCompat.getMainExecutor(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(previewView.width, previewView.height))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setImageQueueDepth(10)
                            .build()
                            .apply {
                                setAnalyzer(executor, TextAnalyzer(
                                    callback = { text ->
                                        textResult = text.text
                                        language =
                                            text.textBlocks.firstOrNull()?.lines?.firstOrNull()?.recognizedLanguage
                                                ?: ""
                                        rect =
                                            text.textBlocks.firstOrNull()?.boundingBox?.toComposeRect()
                                    }
                                ))
                            }

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    }, executor)
                    previewView
                },
            )
//            rect?.let {
//                Canvas(modifier = Modifier.fillMaxSize()) {
//                    drawRect(
//                        color = Black,
//                        style = Stroke(1.dp.toPx()),
//                        topLeft = it.topLeft,
//                        size = it.size
//                    )
//                }
//            }
            Column(
                Modifier
                    .padding(24.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Texto: $textResult",
                    style = TextStyle(color = Color.Green, fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Idioma: $language",
                    style = TextStyle(color = Color.Green, fontSize = 16.sp)
                )
            }
        }
    }
}