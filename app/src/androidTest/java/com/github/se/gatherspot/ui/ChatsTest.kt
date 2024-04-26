package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.chat.ChatViewModel
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.screens.ChatsScreen
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

            eventsList {
                assertExists()
                assertIsDisplayed()
            }
        }

    }
    @OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)
    @Test
    fun chatsAreDisplayedAndScrollable() {

        runTest {
            async {
                FirebaseAuth.getInstance().signInWithEmailAndPassword("mathurinsky@gmail.com", "azerty123A")
            }.await()
        }


        ProfileFirebaseConnection().update(FirebaseAuth.getInstance().currentUser?.uid!!, "registeredEvents", setOf("-NwJSmLmQDUlF9booiq7"))

        composeTestRule.setContent {
            val viewModel = ChatsListViewModel()
            val nav = NavigationActions(rememberNavController())
            Chats(viewModel = viewModel, nav = nav)
        }

        ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
            emptyText {
                assertExists()
                assertIsDisplayed()
            }

            composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 20000)
            eventsList {
                assertIsDisplayed()
                performGesture { swipeUp(400F, 0F, 1000) }
            }
        }
    }


}