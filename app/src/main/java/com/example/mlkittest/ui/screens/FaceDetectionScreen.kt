package com.example.mlkittest.ui.screens

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import com.example.mlkittest.ui.analyzers.FaceAnalyzer

@Composable
fun FaceDetectionScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var smileProbability by rememberSaveable { mutableStateOf(0F) }
    var rightEyeOpenProbability by rememberSaveable { mutableStateOf(0F) }
    var leftEyeOpenProbability by rememberSaveable { mutableStateOf(0F) }

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
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build()

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(previewView.width, previewView.height))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setImageQueueDepth(10)
                            .build()
                            .apply {
                                setAnalyzer(executor, FaceAnalyzer(
                                    callback = { face ->
                                        face.smilingProbability?.let {
                                            smileProbability = it
                                        }
                                        face.rightEyeOpenProbability?.let {
                                            rightEyeOpenProbability = it
                                        }
                                        face.leftEyeOpenProbability?.let {
                                            leftEyeOpenProbability = it
                                        }
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
            Column(
                Modifier
                    .padding(24.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Probabilidade de estar sorrindo: $smileProbability",
                    style = TextStyle(color = Color.Green, fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Probabilidade do olho esquerdo estar aberto: $leftEyeOpenProbability",
                    style = TextStyle(color = Color.Green, fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Probabilidade do olho direito estar aberto: $rightEyeOpenProbability",
                    style = TextStyle(color = Color.Green, fontSize = 16.sp)
                )
            }
        }
    }
}