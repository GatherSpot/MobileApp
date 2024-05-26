package com.github.se.gatherspot.sql

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** The database for the app. */
@Database(entities = [Event::class, IdList::class, Profile::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun EventDao(): EventDao

  abstract fun IdListDao(): IdListDao

  abstract fun ProfileDao(): ProfileDao
}

class Converters {
  @TypeConverter
  fun fromCollection(collection: FirebaseCollection): String {
    return collection.name
  }

  @TypeConverter
  fun toCollection(collection: String): FirebaseCollection {
    return FirebaseCollection.valueOf(collection)
  }

  // we don't care if it isn't same as everywhere else, we just want it small, eg : no / or :
  private val dateFormat = "ddMMyyyy"
  private val timeFormat = "HHmm"

  /**
   * Convert a date to a string
   *
   * @param date the date
   * @return the string
   */
  @TypeConverter
  fun fromDate(date: LocalDate?): String? {
    return date?.format(DateTimeFormatter.ofPattern(dateFormat))
  }

  /**
   * Convert a string to a date
   *
   * @param date the string
   * @return the date
   */
  @TypeConverter
  fun toDate(date: String?): LocalDate? {
    if (date == null) return null
    return LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat))
  }

  /**
   * Convert a time to a string
   *
   * @param time the time
   * @return the string
   */
  @TypeConverter
  fun fromTime(time: LocalTime?): String? {
    return time?.format(DateTimeFormatter.ofPattern(timeFormat))
  }

  /**
   * Convert a string to a time
   *
   * @param time the string
   * @return the time
   */
  @TypeConverter
  fun toTime(time: String?): LocalTime? {
    if (time == null) return null
    return LocalTime.parse(time, DateTimeFormatter.ofPattern(timeFormat))
  }

  /**
   * Convert a set of Interests to a string
   *
   * @param interests the set of Interests
   * @return the string
   */
  @TypeConverter
  fun fromInterests(interests: Set<Interests>): String {
    return Interests.toCompressedString(interests)
  }

  /**
   * Convert a string to a set of Interests
   *
   * @param interests the string
   * @return the set of Interests
   */
  @TypeConverter
  fun toInterests(interests: String): Set<Interests> {
    return Interests.fromCompressedString(interests)
  }

  /**
   * Convert a list of strings to a string
   *
   * @param list the list of strings
   * @return the string
   */
  @TypeConverter
  fun fromListString(list: List<String>): String {
    return list.joinToString(separator = "/")
  }

  /**
   * Convert a string to a list of strings
   *
   * @param list the string
   * @return the list of strings
   */
  @TypeConverter
  fun toListString(list: String): List<String> {
    return if (list.isEmpty()) listOf() else list.split("/")
  }
}
