package com.github.se.gatherspot.ui.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.qrcode.QRCodeUtils

/**
 * Composable for displaying the QR code for an event.
 *
 * @param event The event to generate the QR code for
 */
@Composable
fun EventQRCodeUI(event: Event) {
  var qrCodeBitmap: Bitmap? by remember { mutableStateOf(null) }

  LaunchedEffect(event) {
    val json = "event/${event.id}"
    qrCodeBitmap = QRCodeUtils().generateQRCode(json)
  }

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("EventQRCode"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        qrCodeBitmap?.let { QRCodeDisplay(bitmap = it) } ?: CircularProgressIndicator()
      }
}
