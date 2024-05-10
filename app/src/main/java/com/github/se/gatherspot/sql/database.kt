package com.github.se.gatherspot.sql

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Database(entities = [Event::class,IdList::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun EventDao(): EventDao
  abstract fun IdListDao(): IdListDao
}

class Converters {
  @TypeConverter
  fun fromCollection(collection : FirebaseCollection): String {
    return collection.name
  }
  @TypeConverter
  fun toCollection(collection: String): FirebaseCollection {
    return FirebaseCollection.valueOf(collection)
  }
  // we don't care if it isn't same as everywhere else, we just want it small, eg : no / or :
  private val dateFormat = "ddMMyyyy"
  private val timeFormat = "HHmm"
  @TypeConverter
  fun fromDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.ofPattern(dateFormat))
  }
  @TypeConverter
  fun toDate(date: String): LocalDate {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat))
  }
  @TypeConverter
  fun fromTime(time: LocalTime): String{
    return time.format(DateTimeFormatter.ofPattern(timeFormat))
  }
  @TypeConverter
  fun toTime(time: String): LocalTime {
    return LocalTime.parse(time, DateTimeFormatter.ofPattern(timeFormat))
  }
  @TypeConverter
  fun fromInterests(interests: Set<Interests>): String {
    return Interests.toCompressedString(interests)
  }
  @TypeConverter
  fun toInterests(interests: String): Set<Interests> {
    return Interests.fromCompressedString(interests)
  }
  @TypeConverter
  fun fromListString(list: List<String>): String {
    return list.joinToString(separator = "/")
  }
  @TypeConverter
  fun toListString(list: String): List<String> {
    return list.split("/")
  }

  @OptIn(ExperimentalEncodingApi::class)
  @TypeConverter
  fun fromImageBitmap(image: ImageBitmap): String {
    val bitmap = image.asAndroidBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encode(byteArray)
  }
  @OptIn(ExperimentalEncodingApi::class)
  @TypeConverter
  fun toImageBitmap(base64String: String): ImageBitmap {
    val decodeString = Base64.decode(base64String)
    val bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
    return bitmap.asImageBitmap()

  }
}
