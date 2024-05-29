package com.github.se.gatherspot.run.model

import androidx.compose.ui.graphics.ImageBitmap
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.model.utils.ImageBitmapSerializer
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.junit.Test

class EventTest {

  @Test
  fun testToJson() {
    val event =
        Event(
            id = "1",
            title = "Testing toJson()",
            description = "I have to be converted to Json",
            attendanceMaxCapacity = 10,
            attendanceMinCapacity = 1,
            organizerID = Profile.testParticipant().id,
            categories = setOf(Interests.BOWLING),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf(),
            timeBeginning = LocalTime.of(13, 0),
            timeEnding = LocalTime.of(16, 0),
            image = "")

    val json = event.toJson()

    val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .registerTypeAdapter(ImageBitmap::class.java, ImageBitmapSerializer())
            .create()

    val decodedJson = URLDecoder.decode(json, StandardCharsets.US_ASCII.toString())
    val eventObject = gson.fromJson(decodedJson, Event::class.java)
    assert(eventObject.id == "1")
    assert(eventObject.title == "Testing toJson()")
    assert(eventObject.description == "I have to be converted to Json")
    assert(eventObject.organizerID == Profile.testParticipant().id)
    assert(eventObject.attendanceMinCapacity == 1)
    assert(eventObject.attendanceMaxCapacity == 10)
    assert(eventObject.eventStartDate == LocalDate.of(2024, 4, 14))
    assert(eventObject.eventEndDate == LocalDate.of(2024, 4, 15))
    assert(eventObject.timeBeginning == LocalTime.of(13, 0))
    assert(eventObject.timeEnding == LocalTime.of(16, 0))
  }

  @Test
  fun eventIsOverTestNotOver() {
    val testEvent =
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
    assert(!EventUtils().isEventOver(testEvent))
  }

  @Test
  fun eventIsOverReturnTrue() {
    val event =
        Event(
            id = "testID",
            title = "Test Event",
            description = "This is a test event",
            location = null,
            eventStartDate = LocalDate.of(2020, 4, 12),
            eventEndDate = LocalDate.of(2020, 4, 12),
            timeBeginning = LocalTime.of(10, 0),
            timeEnding = LocalTime.of(12, 0),
            attendanceMaxCapacity = 100,
            attendanceMinCapacity = 10,
            inscriptionLimitDate = LocalDate.of(2020, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            eventStatus = EventStatus.COMPLETED,
            globalRating = null,
            image = "")
    assert(EventUtils().isEventOver(event))
  }
}
