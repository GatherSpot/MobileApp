package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.chat.ChatViewModel
import com.github.se.gatherspot.screens.ChatMessagesScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    val eventId = UUID.randomUUID().toString()
    val eventFirebaseConnection = EventFirebaseConnection()
    val chatViewModel = ChatViewModel(eventId)
    composeTestRule.setContent {
      val event = DefaultEvents.trivialEvent1
      runBlocking { eventFirebaseConnection.add(event) }
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
    FirebaseFirestore.getInstance()
        .collection(chatViewModel.chatMessagesFirebase.CHATS)
        .document(eventId)
        .delete()
        .addOnFailureListener {}
    runBlocking { eventFirebaseConnection.delete(eventId) }
  }
}
