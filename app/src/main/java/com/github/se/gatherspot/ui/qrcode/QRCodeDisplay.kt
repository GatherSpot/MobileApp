package com.github.se.gatherspot.ui.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun QRCodeDisplay(bitmap: Bitmap) {
  Image(
      bitmap = bitmap.asImageBitmap(),
      contentDescription = "Generated QR Code",
      modifier = androidx.compose.ui.Modifier.size(200.dp).padding(16.dp).testTag("QRCodeImage"))
}
