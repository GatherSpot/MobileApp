package com.github.se.gatherspot.ui

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.EnvironmentSetter.Companion.melvinLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.Events
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel
import com.google.firebase.auth.FirebaseAuth
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class EventsTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var uid: String
  private lateinit var ids: List<String>
  private lateinit var viewModel: EventsViewModel
  private val eventFirebaseConnection = EventFirebaseConnection()
  private val futureRegisteredEvent =
      Event(
          id = "EventsTest1",
          title = "deseruisse",
          description = "null",
          location = null,
          eventStartDate = LocalDate.of(2026, 7, 10),
          eventEndDate = LocalDate.of(2026, 7, 11),
          timeBeginning = LocalTime.of(10, 0),
          timeEnding = LocalTime.of(10, 0),
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.CREATED,
          categories = setOf(),
          organizerID = "T1qNNU05QeeqB2OqIBb7GAtQd093",
          registeredUsers = mutableListOf(testLoginUID),
          finalAttendees = listOf(),
          image = "hac",
          globalRating = null)

  private val attendedEvent =
      Event(
          id = "EventsTest2",
          title = "deseruisse",
          description = "null",
          location = null,
          eventStartDate = LocalDate.of(2023, 7, 10),
          eventEndDate = LocalDate.of(2023, 7, 11),
          timeBeginning = LocalTime.of(10, 0),
          timeEnding = LocalTime.of(10, 0),
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.CREATED,
          categories = setOf(),
          organizerID = "T1qNNU05QeeqB2OqIBb7GAtQd093",
          registeredUsers = mutableListOf(testLoginUID),
          finalAttendees = listOf(testLoginUID),
          image = "hac",
          globalRating = null)

  init {
    testLogin()
  }

  @Before
  fun setUp() = runTest {
    uid = FirebaseAuth.getInstance().currentUser!!.uid
    FollowList.follow(uid, uid)
    ids = FollowList.following(uid).elements

    val context = ApplicationProvider.getApplicationContext<Context>()
    melvinLogin()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    eventFirebaseConnection.add(futureRegisteredEvent)
    eventFirebaseConnection.add(attendedEvent)

    viewModel = EventsViewModel(db)
  }

  @Test
  fun testEverythingExists() {
    Thread.sleep(5000)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }

      createMenu {
        assertExists()
        assertHasClickAction()
      }

      myEvents { assertHasClickAction() }
      upComing { assertHasClickAction() }
      fromFollowed { assertHasClickAction() }
      eventFeed { assertHasClickAction() }
    }
  }

  @OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)
  @Test
  fun testEventsAreDisplayedAndScrollable() {

    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      emptyText {
        assertExists()
        assertIsDisplayed()
      }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 20000)
      eventsList {
        assertIsDisplayed()
        performGesture { swipeUp(400F, 0F, 1000) }
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshFunctional() {
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 10000)
    // swipe down to cause refresh
    composeTestRule.onNode(hasTestTag("eventsList")).performTouchInput { swipeDown() }
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("fetching"), 5000)
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 10000)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testInterestsDialogFunctional() {

    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 10000)
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      interestsDialog { assertIsDisplayed() }

      for (category in categories) {
        category { performClick() }
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testFilterWorks() {
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      interestsDialog { assertIsDisplayed() }

      val sport = Interests.SPORT

      categories[sport.ordinal] {
        composeTestRule
        performClick()
      }
      removeFilter { assertHasClickAction() }

      setFilterButton {
        assertHasClickAction()
        performClick()
      }
      composeTestRule.waitForIdle()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("fetch"), 20000)
      composeTestRule.waitUntilDoesNotExist(hasTestTag("fetch"), 20000)
      eventsList { assertExists() }
      assert(viewModel.allEvents.value!!.all { event -> event.categories!!.contains(sport) })
    }
  }
  // NOTE : same as before, will reimplement this when i add the swipe down to refresh

  //  @OptIn(ExperimentalTestApi::class)
  //  @Test
  //  fun testRefreshButtonFunctionalWithFilter() {
  //    assert(FirebaseAuth.getInstance().currentUser != null)
  //    Thread.sleep(5000)
  //    composeTestRule.setContent {
  //      val nav = NavigationActions(rememberNavController())
  //      Events(viewModel = viewModel, nav = nav)
  //    }
  //    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
  //      filterMenu {
  //        assertIsDisplayed()
  //        performClick()
  //      }

  //      interestsDialog { assertIsDisplayed() }

  //      val indexBasketball = Interests.BASKETBALL.ordinal
  //      val indexChess = Interests.CHESS.ordinal

  //      categories[indexBasketball] {
  //        composeTestRule
  //            .onNodeWithTag("dropdown")
  //            .performScrollToNode(
  //                hasTestTag(enumValues<Interests>().toList()[indexBasketball].toString()))
  //        assertExists()
  //        performClick()
  //      }

  //      categories[indexChess] {
  //        composeTestRule
  //            .onNodeWithTag("dropdown")
  //            .performScrollToNode(
  //                hasTestTag(enumValues<Interests>().toList()[indexChess].toString()))
  //        assertExists()
  //        performClick()
  //      }

  //      filterMenu { performClick() }

  //      composeTestRule.waitForIdle()

  //      refresh { performClick() }

  //      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("fetch"), 10000)
  //      composeTestRule.waitUntilDoesNotExist(hasTestTag("fetch"), 10000)

  //      assert(
  //          viewModel.uiState.value.list.all { e ->
  //            if (e.categories == null) {
  //              false
  //            } else {
  //              e.categories!!.contains(Interests.BASKETBALL) ||
  //                  e.categories!!.contains(Interests.CHESS)
  //            }
  //          })
  //    }
  //  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testTabAndPagerWorks() {
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      myEvents { performClick() }
      // Due to the nature of the actual codebase, we just want to test if we fetch the good page,
      // no need to check content
      // as this should be tested in viewModel.
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("myEventsList"), 20000)

      upComing { performClick() }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("upComingEventsList"), 20000)

      fromFollowed { performClick() }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("followedEventsList"), 20000)
    }
  }
  /*
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun entireCreationFlow() {
    val viewModel = EventsViewModel()
    Thread.sleep(6000)
    assert(viewModel.uiState.value.list.isNotEmpty())

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "events", route = "home") {
          composable("events") { Events(viewModel, NavigationActions(navController)) }
        }
        navigation(startDestination = "form", route = "createEvent") {
          composable("form") {
            CreateEvent(
                nav = NavigationActions(navController),
                eventUtils = EventUtils(),
                viewModel = viewModel)
          }
        }
        navigation(startDestination = "view", route = "event/{eventJson}") {
          composable("view") { backStackEntry ->
            val gson: Gson =
                GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
                    .create()
            val eventObject =
                gson.fromJson(backStackEntry.arguments?.getString("eventJson"), Event::class.java)
            EventUI(
                event = eventObject,
                navActions = NavigationActions(navController),
                registrationViewModel = EventRegistrationViewModel(eventObject.registeredUsers),
                eventsViewModel = viewModel)
          }
        }
        navigation(startDestination = "edit", route = "editEvent/{eventJson}") {
          composable("edit") { backStackEntry ->
            val gson: Gson =
                GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
                    .create()

            val eventObject =
                gson.fromJson(backStackEntry.arguments?.getString("eventJson"), Event::class.java)

            EditEvent(
                event = eventObject,
                eventUtils = EventUtils(),
                nav = NavigationActions(navController),
                viewModel = viewModel)
          }
        }
      }
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) { createMenu.performClick() }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      eventTitle.performScrollTo()
      eventTitle.performTextInput("Basketball Game")
      Espresso.closeSoftKeyboard()
      eventDescription.performTextInput("Ayo, 5v5: Come show your skills")
      Espresso.closeSoftKeyboard()
      eventStartDate.performTextInput("10/07/2024")
      Espresso.closeSoftKeyboard()
      eventEndDate.performTextInput("10/07/2024")
      Espresso.closeSoftKeyboard()
      eventTimeStart.performTextInput("13:00")
      Espresso.closeSoftKeyboard()
      eventTimeEnd.performTextInput("19:00")
      Espresso.closeSoftKeyboard()
      eventLocation.performTextInput("Bussy-Saint-Georges")
      Espresso.closeSoftKeyboard()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("MenuItem"), 6000)
      Espresso.closeSoftKeyboard()
      locationProposition { performClick() }
      Espresso.closeSoftKeyboard()
      eventMaxAttendees.performTextInput("10")
      Espresso.closeSoftKeyboard()
      eventMinAttendees.performTextInput("5")
      Espresso.closeSoftKeyboard()
      eventInscriptionLimitDate.performTextInput("10/06/2024")
      Espresso.closeSoftKeyboard()
      eventInscriptionLimitTime.performTextInput("09:00")
      Espresso.closeSoftKeyboard()
      eventSaveButton.performScrollTo()
      eventSaveButton.performClick()
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu { performClick() }
      myEvents {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag("myEvents"))
        performClick()
      }
      assert(
          viewModel.uiState.value.list.any { event ->
            event.description == "Ayo, 5v5: Come show your skills"
          })
      eventCreated { performClick() }
    }

    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      editEventButton { performClick() }
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventDescription { assertTextContains("Ayo, 5v5: Come show your skills") }
    }

    val event = viewModel.uiState.value.list.filter { e -> e.title == "Basketball Game" }[0]
    EventFirebaseConnection().delete(event.id)

     */
}

/*
ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) { createMenu.performClick() }

ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
  composeTestRule.waitUntilAtLeastOneExists(hasTestTag("inputTitle"), 5000)
  eventTitle.performTextInput("Basketball Game")
  Espresso.closeSoftKeyboard()
  eventDescription.performTextInput("Ayo, 5v5: Come show your skills")
  Espresso.closeSoftKeyboard()
  eventStartDate.performTextInput("10/07/2024")
  Espresso.closeSoftKeyboard()
  eventEndDate.performTextInput("10/07/2024")
  Espresso.closeSoftKeyboard()
  eventTimeStart.performTextInput("13:00")
  Espresso.closeSoftKeyboard()
  eventTimeEnd.performTextInput("19:00")
  Espresso.closeSoftKeyboard()
  eventLocation.performTextInput("Bussy-Saint-Georges")
  Espresso.closeSoftKeyboard()
  composeTestRule.waitUntilAtLeastOneExists(hasTestTag("MenuItem"), 6000)
  Espresso.closeSoftKeyboard()
  locationProposition { performClick() }
  Espresso.closeSoftKeyboard()
  eventMaxAttendees.performTextInput("10")
  Espresso.closeSoftKeyboard()
  eventMinAttendees.performTextInput("5")
  Espresso.closeSoftKeyboard()
  eventInscriptionLimitDate.performTextInput("10/06/2024")
  Espresso.closeSoftKeyboard()
  eventInscriptionLimitTime.performTextInput("09:00")
  Espresso.closeSoftKeyboard()
  eventSaveButton.performScrollTo()
  eventSaveButton.performClick()
}

 */
