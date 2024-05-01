package com.github.se.gatherspot.model.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
      context: JsonSerializationContext
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
      context: JsonDeserializationContext
  ): LocalDate {
    return LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
  }
}

// Custom serializer for LocalDateTime
class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
  override fun serialize(
      src: LocalDateTime,
      typeOfSrc: Type,
      context: JsonSerializationContext
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
      context: JsonDeserializationContext
  ): LocalDateTime {
    return LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  }
}

/**
 * Custom serializer for Bitmap images
 */
class BitmapSerializer : JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {
    override fun serialize(
        src: Bitmap?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(BitmapImageConverter.fromBitmap(src))
        } else {
            JsonPrimitive("")
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Bitmap? {
        return if (json != null && json.asString.isNotEmpty()) {
            BitmapImageConverter.toBitmap(json.asString)
        } else {
            null
        }
    }
}

object BitmapImageConverter {
    // Converter for Bitmap to String
    fun fromBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    }

    // Converter for String to Bitmap
    fun toBitmap(stringPicture: String): Bitmap {
        val byteArray = Base64.getDecoder().decode(stringPicture)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
