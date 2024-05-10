package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.screens.ChatsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatsTest {

  @get:Rule val composeTestRule = createComposeRule()

  lateinit var id: String

  @Before
  fun testLogin() = runBlocking {
    Firebase.auth
        .signInWithEmailAndPassword("neverdeleted@mail.com", "GatherSpot,2024;")
        .addOnSuccessListener { id = Firebase.auth.currentUser?.uid ?: "" }

    EventFirebaseConnection()
        .add(
            Event(
                id = "idTestEvent",
                title = "Event Title",
                description =
                    "Hello: I am a description of the event just saying that I would love to say" +
                        "that Messi is not the best player in the world, but I can't. I am sorry.",
                attendanceMaxCapacity = 5,
                attendanceMinCapacity = 1,
                categories = setOf(Interests.BASKETBALL),
                eventEndDate = LocalDate.of(2024, 4, 15),
                eventStartDate = LocalDate.of(2024, 4, 14),
                globalRating = 4,
                inscriptionLimitDate = LocalDate.of(2024, 4, 11),
                inscriptionLimitTime = LocalTime.of(23, 59),
                location = null,
                registeredUsers = mutableListOf(),
                timeBeginning = LocalTime.of(10, 0),
                timeEnding = LocalTime.of(12, 0),
                image = ""))
  }

  @After
  fun testLoginCleanUp() {
    Firebase.auth.signOut()
  }

  @Test
  fun testEverythingExists() {

    composeTestRule.waitForIdle()

    composeTestRule.setContent {
      val viewModel = ChatsListViewModel()
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
      topBar {
        assertExists()
        assertIsDisplayed()
      }

      empty {
        assertExists()
        assertIsDisplayed()
      }
    }
  }

  @OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)
  @Test
  fun chatsAreDisplayedAndScrollable() {

    composeTestRule.waitForIdle()

    val viewModel = ChatsListViewModel()
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

    runBlocking { EventFirebaseConnection().addRegisteredUser("idTestEvent", id) }

    ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 20000)
      eventsList {
        assertExists()
        assertIsDisplayed()
        performGesture { swipeUp(400F, 0F, 1000) }
      }
    }
  }
}
