package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.NFCService
import com.github.se.gatherspot.model.NFCStatus
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.screens.NFCScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NFCUITest {
  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      NFCUI(
          NavigationActions(rememberNavController()),
          NFCService(NFCStatus.ORGANIZER),
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
              image = ""))
    }
    ComposeScreen.onComposeScreen<NFCScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nfc"), 10000)

      text { assertExists() }
    }
  }
}
