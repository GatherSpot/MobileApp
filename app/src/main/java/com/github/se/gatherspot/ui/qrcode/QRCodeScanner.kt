package com.github.se.gatherspot.ui.qrcode

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.model.qrcode.BarCodeAnalyser
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScanner(navigationActions: NavigationActions) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.testTag("QRCodeScanner")) {
    Spacer(modifier = Modifier.height(10.dp))

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    Button(onClick = { cameraPermissionState.launchPermissionRequest() }, modifier = Modifier.testTag("cameraPermissionButton")) {
      Text(text = "Camera Permission")
    }

    Spacer(modifier = Modifier.height(10.dp))

    CameraPreview(navigationActions = navigationActions)
  }
}

@Composable
fun CameraPreview(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  var preview by remember { mutableStateOf<Preview?>(null) }
  val barCodeVal = remember { mutableStateOf("") }

  AndroidView(
      factory = { AndroidViewContext ->
        PreviewView(AndroidViewContext).apply {
          this.scaleType = PreviewView.ScaleType.FILL_CENTER
          layoutParams =
              ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.MATCH_PARENT,
              )
          implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
      },
      modifier = Modifier.fillMaxSize().testTag("CameraPreview"),
      update = { previewView ->
        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
              preview =
                  Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                  }
              val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
              val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                barcodes.forEach { barcode ->
                  barcode.rawValue?.let { barcodeValue ->
                    barCodeVal.value = barcodeValue
                    Toast.makeText(context, barcodeValue, Toast.LENGTH_SHORT).show()
                  }
                }
                if (barcodes.isNotEmpty()) {
                  val code = barcodes[0].rawValue
                  if (code != null) {
                    BarCodeAnalyser.analyseAppQRCode(code, navigationActions)
                  }
                }
              }
              val imageAnalysis: ImageAnalysis =
                  ImageAnalysis.Builder()
                      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                      .build()
                      .also { it.setAnalyzer(cameraExecutor, barcodeAnalyser) }

              try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis)
              } catch (e: Exception) {
                Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
              }
            },
            ContextCompat.getMainExecutor(context))
      })
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewQRCodeScanner() {
  QRCodeScanner(navigationActions = NavigationActions(rememberNavController()))
}
