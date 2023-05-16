package com.example.mlkittest.ui.analyzers

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    val callback: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.let { barcode ->
                        callback.invoke(barcode)
                    }
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