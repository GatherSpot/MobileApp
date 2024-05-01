package com.github.se.gatherspot.utils

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.github.se.gatherspot.model.utils.ImageBitmapSerializer
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalDateTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalDateTimeSerializer
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

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
    fun testLocalDateTimeSerializer() {
        val localDateTime = LocalDateTime.of(2022, 12, 31, 23, 59)
        val serializer = LocalDateTimeSerializer()
        val jsonElement = serializer.serialize(localDateTime, localDateTime::class.java, null)
        assertEquals(JsonPrimitive("2022-12-31T23:59:00"), jsonElement)
    }

    @Test
    fun testLocalDateTimeDeserializer(){
        val jsonElement = JsonPrimitive("2022-12-31T23:59:00")
        val deserializer = LocalDateTimeDeserializer()
        val localDateTime = deserializer.deserialize(jsonElement, LocalDateTime::class.java, null)
        assertEquals(LocalDateTime.of(2022, 12, 31, 23, 59), localDateTime)
    }

    @Test
    fun testImageBitmapSerializer() {
        val imageBitmap = ImageBitmap(30, 30)
        val serializer = ImageBitmapSerializer()
        val jsonElement = serializer.serialize(imageBitmap, ImageBitmap::class.java, null)
        Log.d("ImageBitmapSerializer", jsonElement.toString())
        val deserializer = ImageBitmapSerializer()
        val deserializedImageBitmap = deserializer.deserialize(jsonElement, ImageBitmap::class.java, null)
        assertEquals(imageBitmap.height, deserializedImageBitmap!!.height)
        assertEquals(imageBitmap.width, deserializedImageBitmap.width)
    }
}