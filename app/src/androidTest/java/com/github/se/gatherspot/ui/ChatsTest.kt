package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.screens.ChatsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.Chats
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.delay
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
  private var testEvent =
      Event(
          id = "idFilteringChat",
          title = "Here to test filtering of chats",
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
          image = "")

  @Before
  fun setUp() = runBlocking {
    EnvironmentSetter.testLogin()
    id = Firebase.auth.currentUser!!.uid
    EventFirebaseConnection().add(testEvent)
  }

  @After
  fun cleanUp() {
    runBlocking {
      EnvironmentSetter.testLoginCleanUp()
      EventFirebaseConnection().delete(testEvent.id)
    }
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

      refresh {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)
  @Test
  fun chatsAreDisplayedAndScrollable() {

    runBlocking { IdList.empty(id, FirebaseCollection.REGISTERED_EVENTS).add(testEvent.id) }

    composeTestRule.waitForIdle()
    val viewModel = ChatsListViewModel()
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 20000)
      eventsList {
        assertExists()
        assertIsDisplayed()
        performGesture { swipeUp(400F, 0F, 1000) }
      }
    }

    runBlocking { IdList.empty(id, FirebaseCollection.REGISTERED_EVENTS).remove(testEvent.id) }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun filteringAndRefreshWorkCorrectly() {
    runBlocking {
      IdList.empty(id, FirebaseCollection.REGISTERED_EVENTS).add(testEvent.id)
      EventFirebaseConnection().addRegisteredUser(testEvent.id, id)
      delay(5000)
      val viewModel = ChatsListViewModel()
      composeTestRule.setContent {
        val nav = NavigationActions(rememberNavController())
        Chats(viewModel = viewModel, nav = nav)
      }

      delay(5000)
      ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
        searchBar { performTextInput(testEvent.title) }
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 5000)
        composeTestRule.waitForIdle()
        assert(viewModel.allEvents.value!!.size == 1)

        searchBar {
          performTextClearance()
          performTextInput("NoneSense")
        }

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("emptyText"), 5000)
        composeTestRule.waitForIdle()
        assert(viewModel.allEvents.value!!.isEmpty())

        refresh { performClick() }
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 5000)
        composeTestRule.waitForIdle()
        assert(viewModel.allEvents.value!!.isNotEmpty())
      }

      IdList.empty(id, FirebaseCollection.REGISTERED_EVENTS).remove(testEvent.id)
      EventFirebaseConnection().removeRegisteredUser(testEvent.id, id)
    }
  }
}
