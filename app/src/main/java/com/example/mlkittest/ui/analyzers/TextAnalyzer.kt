package com.example.mlkittest.ui.analyzers

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextAnalyzer(
    val callback: (Text) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        mediaImage?.let {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            recognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    callback.invoke(text)
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