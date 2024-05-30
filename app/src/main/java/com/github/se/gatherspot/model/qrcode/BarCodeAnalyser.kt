package com.github.se.gatherspot.model.qrcode

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.TimeUnit

/**
 * Analyser for detecting barcodes in images.
 *
 * @property onBarcodeDetected Callback function to be called when a barcode is detected.
 */
@SuppressLint("UnsafeOptInUsageError")
class BarCodeAnalyser(
    private val onBarcodeDetected: @Composable (barcodes: List<Barcode>) -> Unit,
) : ImageAnalysis.Analyzer {
  private var lastAnalyzedTimeStamp = 0L

  /**
   * Search the image for barcodes.
   *
   * @param image ImageProxy The image to analyze.
   */
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
}
