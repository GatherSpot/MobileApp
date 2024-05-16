package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.model.chat.ChatViewModel
import com.github.se.gatherspot.screens.ChatMessagesScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.utils.MockChatMessagesFirebaseConnection
import com.github.se.gatherspot.utils.MockEventFirebaseConnection
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ChatUITest {

  @get:Rule val composeTestRule = createComposeRule()

  // Here, we use mockito to disable the listenToMessages function, this way we effectively disable unwanted Firebase interactions + mock as we want

  @Test
  fun testEverythingExists() {
    val eventId = DefaultEvents.trivialEvent1.id
    val chatViewModel = ChatViewModel(eventId, MockEventFirebaseConnection(), MockChatMessagesFirebaseConnection())
    composeTestRule.setContent {
      chatViewModel.addMessage(UUID.randomUUID().toString(), "user1", "Hello")
      ChatUI(chatViewModel, "user1", NavigationActions(rememberNavController()))
    }
    ComposeScreen.onComposeScreen<ChatMessagesScreen>(composeTestRule) {
      chatScaffold.assertExists()
      messagesList.assertExists()
      spacer.assertExists()
      boxChatMessageCard.assertExists()
      chatMessageCard.assertExists()
      chatTopBar.assertExists()
      inputMessage.assertExists()
      sendButton.assertExists()

      chatScaffold.assertIsDisplayed()
      messagesList.assertIsDisplayed()
      boxChatMessageCard.assertIsDisplayed()
      chatMessageCard.assertIsDisplayed()
      chatTopBar.assertIsDisplayed()
      inputMessage.assertIsDisplayed()
      sendButton.assertIsDisplayed()
    }
  }
}
