package com.github.se.gatherspot.ui.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.qrcode.BarCodeAnalyser
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.launch

/**
 * Composable for the QR code scanner.
 *
 * @param navigationActions The navigation actions
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScanner(navigationActions: NavigationActions) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.testTag("QRCodeScanner")) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
          IconButton(
              onClick = { navigationActions.goBack() },
              modifier = Modifier.testTag("goBackButton")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back")
              }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

        Button(
            onClick = { cameraPermissionState.launchPermissionRequest() },
            modifier = Modifier.testTag("cameraPermissionButton")) {
              Text(text = "Camera Permission")
            }

        Spacer(modifier = Modifier.height(10.dp))

        CameraPreview(navigationActions = navigationActions)
      }
}

/**
 * Composable for the camera preview when scanning QR codes
 *
 * @param navigationActions The navigation actions
 */
@Composable
fun CameraPreview(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
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
                    coroutineScope.launch {
                      val navString = analyseAppQRCode(barcodeValue, context)
                      if (navString != "") {
                        navigationActions.controller.navigate(navString)
                      } else {
                        navigationActions.controller.navigate("events")
                      }
                    }
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

/**
 * Analyse the QR code and return a string equal to a navigation route.
 *
 * @param text The text from the QR code
 * @return The navigation string
 */
@SuppressLint("SuspiciousIndentation")
suspend fun analyseAppQRCode(text: String, context: Context): String {
  if (!isUserOnline(context)) {
    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
    return ""
  }
  val parts = text.split("/")
  if (parts[0] == "event") {
    val uid = parts[1]
    val event = EventFirebaseConnection().fetch(uid)
    return if (event == null) {
      ""
    } else {
      "event/${event.toJson()}"
    }
  }
  return if (parts.size == 2) {
    if (parts[0] == "profile") {
      "viewProfile/${parts[1]}"
    } else {
      ""
    }
  } else {
    ""
  }
}

fun isUserOnline(context: Context): Boolean {
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork ?: return false
  val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
  return when {
    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
    else -> false
  }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun QRCodeScannerPreview() {
  QRCodeScanner(NavigationActions(rememberNavController()))
}
