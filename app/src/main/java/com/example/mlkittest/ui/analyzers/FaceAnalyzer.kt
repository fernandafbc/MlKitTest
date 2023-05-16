package com.example.mlkittest.ui.analyzers

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


class FaceAnalyzer(
    val callback: (Face) -> Unit,
) : ImageAnalysis.Analyzer {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val detector = FaceDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    faces.firstOrNull()?.let { it1 -> callback.invoke(it1) }
                    image.close()
                }
                .addOnFailureListener {
                    image.close()
                }
                .addOnCompleteListener {
                    image.close()
                }
        }
    }
}