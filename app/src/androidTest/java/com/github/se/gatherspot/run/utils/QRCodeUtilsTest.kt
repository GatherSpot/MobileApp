package com.github.se.gatherspot.run.utils

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.model.qrcode.QRCodeUtils
import com.github.se.gatherspot.model.utils.ImageBitmapSerializer
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

  @Test
  fun test() {
    val event =
        Event(
            id = "testID",
            title = "Test Event",
            description = "This is a test event",
            location = Location(0.0, 0.0, "Test Location"),
            eventStartDate =
                LocalDate.parse(
                    "12/04/2026",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            eventEndDate =
                LocalDate.parse(
                    "12/05/2026",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            timeBeginning =
                LocalTime.parse(
                    "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            timeEnding =
                LocalTime.parse(
                    "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            attendanceMaxCapacity = 100,
            attendanceMinCapacity = 10,
            inscriptionLimitDate =
                LocalDate.parse(
                    "10/04/2025",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            inscriptionLimitTime =
                LocalTime.parse(
                    "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            eventStatus = EventStatus.CREATED,
            globalRating = null,
            image = "")
    val gson1: Gson =
        GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .registerTypeAdapter(ImageBitmap::class.java, ImageBitmapSerializer())
            .create()
    val json = gson1.toJson(event)
    Log.w("Event", json)
    val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .create()
    val eventObject = gson.fromJson(json, Event::class.java)
    Log.e("Event", "$eventObject")
  }
}
