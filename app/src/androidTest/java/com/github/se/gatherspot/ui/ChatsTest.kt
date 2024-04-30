package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.screens.ChatsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatsTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    FirebaseAuth.getInstance().signOut()
  }

  @After
  fun tearDown() {
    FirebaseAuth.getInstance().signOut()
  }
    @Test
    fun testEverythingExists() {
      composeTestRule.setContent {
        val viewModel = ChatsListViewModel()
        val nav = NavigationActions(rememberNavController())
        Chats(viewModel = viewModel, nav = nav)
      }

      ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
        createText {
          assertExists()
          assertIsDisplayed()
        }

        createMenu {
          assertExists()
          assertIsDisplayed()
          assertHasClickAction()
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
      val firebase = FirebaseAuth.getInstance()

      firebase.signOut()
      firebase.createUserWithEmailAndPassword("GatherSpotTest", "azerty123A")
      firebase.signInWithEmailAndPassword("GatherSpotTest", "azerty123A")
      var uid = firebase.currentUser?.uid ?: ""
      runBlocking {
        async {
          firebase
            .signInWithEmailAndPassword("mathurinsky@gmail.com", "azerty123A")
        }.await()
        firebase.updateCurrentUser(firebase.currentUser!!)

        uid = firebase.currentUser?.uid!!

        ProfileFirebaseConnection()
          .add(
            com.github.se.gatherspot.model.Profile(
              "GatherSpotTest", "", "", uid, setOf(Interests.ART)
            )
          )

        ProfileFirebaseConnection().update(uid, "registeredEvents", setOf("-NwJSmLmQDUlF9booiq7"))
      }


      val viewModel = ChatsListViewModel()
      composeTestRule.setContent {
        val nav = NavigationActions(rememberNavController())
        Chats(viewModel = viewModel, nav = nav)
      }

      runTest { viewModel.fetchNext(uid) }
      ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 20000)
        eventsList {
          assertExists()
          assertIsDisplayed()
          performGesture { swipeUp(400F, 0F, 1000) }
        }
      }
      FirebaseAuth.getInstance().signOut()
    }
  }
