package com.github.se.gatherspot.ui.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.qrcode.QRCodeUtils

@Composable
fun ProfileQRCodeUI(profile: Profile) {
  var qrCodeBitmap: Bitmap? by remember { mutableStateOf(null) }

  LaunchedEffect(profile) {
    val json = "profile/" + profile.id
    qrCodeBitmap = QRCodeUtils().generateQRCode(json)
  }

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("ProfileQRCode"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        qrCodeBitmap?.let { QRCodeDisplay(bitmap = it) } ?: CircularProgressIndicator()
      }
}
