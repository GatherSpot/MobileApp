package com.github.se.gatherspot.ui.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
      modifier = Modifier.size(300.dp).padding(16.dp).testTag("QRCodeImage"))
}
