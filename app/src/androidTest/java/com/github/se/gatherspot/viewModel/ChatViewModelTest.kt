package com.github.se.gatherspot.viewModel

import com.github.se.gatherspot.model.chat.ChatViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatViewModelTest {

  private lateinit var viewModel: ChatViewModel
  private val eventId = "testEvent"

  @Before
  fun setup() {
    viewModel = ChatViewModel(eventId)
  }

  @Test
  fun testAddAndRetrieveMessage(): Unit = runBlocking {
    val messageText = "Test message"
    val senderId = "testUser"
    val messageId = UUID.randomUUID().toString()
    viewModel.addMessage(messageId, senderId, messageText)

    // Allow time for the message to be processed in Firestore
    delay(1000)

    val messages = viewModel.messages.value

    assertTrue(messages.any { it.message == messageText && it.senderId == senderId })

    // Cleanup: remove added message
    val messageToClean =
        messages.firstOrNull { it.message == messageText && it.senderId == senderId }
    messageToClean?.let {
      FirebaseFirestore.getInstance()
          .collection("chats")
          .document(eventId)
          .collection("messages")
          .document(it.id)
          .delete()
          .await()
    }
  }

  @Test
  fun testMessageDeletion() = runBlocking {
    val senderId = "testUser"
    val messageText = "Message to delete"
    val messageId = UUID.randomUUID().toString()
    viewModel.addMessage(messageId, senderId, messageText)

    // Wait for the message to be added
    delay(1000)

    viewModel.removeMessage(messageId)

    // Allow time for the deletion to be processed
    delay(1000)

    val messages = viewModel.messages.value
    assertFalse(messages.any { it.id == messageId })
  }
}
