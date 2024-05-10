package com.github.se.gatherspot.model.qrcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

// Utility class for generating QR codes and serializing/deserializing objects
class QRCodeUtils {

  // Generate a QR code bitmap from a given string
  fun generateQRCode(content: String, size: Int = 512): Bitmap {
    val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
      for (x in 0 until size) {
        for (y in 0 until size) {
          setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
        }
      }
    }
  }
}
