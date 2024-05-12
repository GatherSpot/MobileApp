package com.github.se.gatherspot.model.qrcode

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.TimeUnit

@SuppressLint("UnsafeOptInUsageError")
class BarCodeAnalyser(
    private val onBarcodeDetected: (barcodes: List<Barcode>) -> Unit,
) : ImageAnalysis.Analyzer {
  private var lastAnalyzedTimeStamp = 0L

  override fun analyze(image: ImageProxy) {
    val currentTimestamp = System.currentTimeMillis()
    if (currentTimestamp - lastAnalyzedTimeStamp >= TimeUnit.SECONDS.toMillis(1)) {
      image.image?.let { imageToAnalyze ->
        val options =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        val barcodeScanner = BarcodeScanning.getClient(options)
        val imageToProcess =
            InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

        barcodeScanner
            .process(imageToProcess)
            .addOnSuccessListener { barcodes ->
              if (barcodes.isNotEmpty()) {
                onBarcodeDetected(barcodes)
              } else {
                Log.d("TAG", "analyze: No barcode Scanned")
              }
            }
            .addOnFailureListener { exception ->
              Log.d("TAG", "BarcodeAnalyser: Something went wrong $exception")
            }
            .addOnCompleteListener { image.close() }
      }
      lastAnalyzedTimeStamp = currentTimestamp
    } else {
      image.close()
    }
  }

  companion object {
    fun analyseAppQRCode(text: String, navigationActions: NavigationActions) {
      val parts = text.split("/")
      if (parts.size == 2) {
        if (parts[0] == "event") {
          navigationActions.controller.navigate("event/${parts[1]}")
        } else if (parts[0] == "profile") {
          navigationActions.controller.navigate("viewProfile/${parts[1]}")
        }
      }
    }
  }
}
