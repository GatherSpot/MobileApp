package com.github.se.gatherspot.model.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

// Custom serializer for LocalDate
class LocalDateSerializer : JsonSerializer<LocalDate> {
  override fun serialize(
      src: LocalDate,
      typeOfSrc: Type,
      context: JsonSerializationContext?
  ): JsonElement {
    return JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE))
  }
}

// Custom deserializer for LocalDate
class LocalDateDeserializer : JsonDeserializer<LocalDate> {
  @Throws(JsonParseException::class)
  override fun deserialize(
      json: JsonElement,
      typeOfT: Type,
      context: JsonDeserializationContext?
  ): LocalDate {
    return LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
  }
}

// Custom serializer for LocalDateTime
class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
  override fun serialize(
      src: LocalDateTime,
      typeOfSrc: Type,
      context: JsonSerializationContext?
  ): JsonElement {
    return JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  }
}

// Custom deserializer for LocalDateTime
class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
  @Throws(JsonParseException::class)
  override fun deserialize(
      json: JsonElement,
      typeOfT: Type,
      context: JsonDeserializationContext?
  ): LocalDateTime {
    return LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  }
}

/** Custom serializer for Bitmap images */
class ImageBitmapSerializer : JsonSerializer<ImageBitmap>, JsonDeserializer<ImageBitmap> {
  override fun serialize(
      src: ImageBitmap?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
  ): JsonElement {
    return if (src != null) {
      JsonPrimitive(ImageBitmapConverter.fromImageBitmap(src))
    } else {
      JsonPrimitive("")
    }
  }

  @Throws(JsonParseException::class)
  override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
  ): ImageBitmap? {
    return if (json != null && json.asString.isNotEmpty()) {
      ImageBitmapConverter.toImageBitmap(json.asString)
    } else {
      null
    }
  }
}

object ImageBitmapConverter {
  // Converter for Bitmap to String
  fun fromImageBitmap(imageBitmap: ImageBitmap): String {
    // convert imageBitmap to Bitmap
    val bitmap = imageBitmap.asAndroidBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
  }

  // Converter for String to Bitmap
  fun toImageBitmap(stringPicture: String): ImageBitmap {
    val byteArray = Base64.getDecoder().decode(stringPicture)
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    return bitmap.asImageBitmap()
  }
}
