package com.github.se.gatherspot.run.utils

import com.github.se.gatherspot.model.qrcode.QRCodeUtils
import junit.framework.TestCase.assertEquals
import org.junit.Test

class QRCodeUtilsTest {
  @Test
  fun generatesNotNullBitmap() {
    // Given
    val content = "https://example.com"
    val size = 512
    val utils = QRCodeUtils()

    // When
    val bitmap = utils.generateQRCode(content, size)

    assert(bitmap != null)

    assertEquals(size, bitmap.width)
    assertEquals(size, bitmap.height)
  }

  @Test
  fun hasCorrectColor() {
    val content = "Test QR Code"
    val size = 256
    val utils = QRCodeUtils()

    val bitmap = utils.generateQRCode(content, size)

    assertEquals(-0x1, bitmap.getPixel(size / 2, size / 2))
  }
}
