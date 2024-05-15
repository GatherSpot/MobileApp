package com.github.se.gatherspot.model

import androidx.compose.ui.graphics.ImageBitmap
import com.github.se.gatherspot.model.event.Event
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
}
