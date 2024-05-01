package com.github.se.gatherspot.cache

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.DraftEvent
import com.github.se.gatherspot.model.location.Location
import org.junit.Test

class LocalStorageTest {
  @Test fun loadDraftEventTest() {}

  @Test
  fun storeDraftEventTest() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStorage = LocalStorage(context)
    val draftEvent =
        DraftEvent(
            "organiserId",
            "title",
            "description",
            Location(0.0, 0.0, "Malibu"),
            "eventStartDate",
            "eventEndDate",
            "timeBeginning",
            "timeEnding",
            "attendanceMaxCapacity",
            "attendanceMinCapacity",
            "inscriptionLimitDate",
            "inscriptionLimitTime",
            setOf(Interests.SPORT, Interests.FOOTBALL, Interests.BASKETBALL, Interests.TENNIS),
            null)

    localStorage.storeDraftEvent(draftEvent)
    val loadedDraftEvent = localStorage.loadDraftEvent()
    assert(loadedDraftEvent == draftEvent)
    localStorage.deleteDraftEvent()
  }

  @Test
  fun deleteDraftEventTest() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStorage = LocalStorage(context)
    val draftEvent =
        DraftEvent(
            "organiserId",
            "title",
            "description",
            null,
            "eventStartDate",
            "eventEndDate",
            "timeBeginning",
            "timeEnding",
            "attendanceMaxCapacity",
            "attendanceMinCapacity",
            "inscriptionLimitDate",
            "inscriptionLimitTime",
            emptySet(),
            null)

    localStorage.storeDraftEvent(draftEvent)
    localStorage.deleteDraftEvent()
    val loadedDraftEvent = localStorage.loadDraftEvent()
    assert(loadedDraftEvent == null)
  }
}
