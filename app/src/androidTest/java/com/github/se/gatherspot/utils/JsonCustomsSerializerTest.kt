package com.github.se.gatherspot.utils

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.github.se.gatherspot.model.utils.ImageBitmapSerializer
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.google.gson.JsonPrimitive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonCustomsSerializerTest {

  @Test
  fun testLocalDateSerializer() {
    val localDate = LocalDate.of(2022, 12, 31)
    val serializer = LocalDateSerializer()
    val jsonElement = serializer.serialize(localDate, localDate::class.java, null)
    assertEquals(JsonPrimitive("2022-12-31"), jsonElement)
  }

  @Test
  fun testLocalDateDeserializer() {
    val jsonElement = JsonPrimitive("2022-12-31")
    val deserializer = LocalDateDeserializer()
    val localDate = deserializer.deserialize(jsonElement, LocalDate::class.java, null)
    assertEquals(LocalDate.of(2022, 12, 31), localDate)
  }

  @Test
  fun testLocalTimeSerializer() {
    val localTime = LocalTime.of(10, 15)
    val serializer = LocalTimeSerializer()
    val jsonElement = serializer.serialize(localTime, LocalTime::class.java, null)
    assertEquals(JsonPrimitive("10:15:00"), jsonElement)
  }

  @Test
  fun testLocalTimeDeserializer() {
    val jsonElement = JsonPrimitive("10:15")
    val deserializer = LocalTimeDeserializer()
    val localTime = deserializer.deserialize(jsonElement, LocalDateTime::class.java, null)
    assertEquals(LocalTime.of(10, 15), localTime)
  }

  @Test
  fun testImageBitmapSerializer() {
    val imageBitmap = ImageBitmap(30, 30)
    val serializer = ImageBitmapSerializer()
    val jsonElement = serializer.serialize(imageBitmap, ImageBitmap::class.java, null)
    Log.d("ImageBitmapSerializer", jsonElement.toString())
    val deserializer = ImageBitmapSerializer()
    val deserializedImageBitmap =
        deserializer.deserialize(jsonElement, ImageBitmap::class.java, null)
    assertEquals(imageBitmap.height, deserializedImageBitmap!!.height)
    assertEquals(imageBitmap.width, deserializedImageBitmap.width)
  }
}
