package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.chat.ChatViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.screens.ChatMessagesScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    val eventId = UUID.randomUUID().toString()
    val eventFirebaseConnection = com.github.se.gatherspot.EventFirebaseConnection()
    val chatViewModel = ChatViewModel(eventId)
    composeTestRule.setContent {
      val event =
          Event(
              id = eventId,
              title = "Test Event",
              description = "This is a test event",
              location = Location(0.0, 0.0, "Test Location"),
              eventStartDate =
                  LocalDate.parse(
                      "12/04/2026",
                      DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT)),
              eventEndDate =
                  LocalDate.parse(
                      "12/05/2026",
                      DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT)),
              timeBeginning =
                  LocalTime.parse(
                      "10:00", DateTimeFormatter.ofPattern(eventFirebaseConnection.TIME_FORMAT)),
              timeEnding =
                  LocalTime.parse(
                      "12:00", DateTimeFormatter.ofPattern(eventFirebaseConnection.TIME_FORMAT)),
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              inscriptionLimitDate =
                  LocalDate.parse(
                      "10/04/2025",
                      DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT)),
              inscriptionLimitTime =
                  LocalTime.parse(
                      "09:00", DateTimeFormatter.ofPattern(eventFirebaseConnection.TIME_FORMAT)),
              eventStatus = EventStatus.DRAFT,
              categories = setOf(Interests.CHESS),
              registeredUsers = mutableListOf("my_id"),
              finalAttendees = emptyList(),
              images = null,
              globalRating = null)
      eventFirebaseConnection.add(event)
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
    eventFirebaseConnection.delete(eventId)
  }
}
