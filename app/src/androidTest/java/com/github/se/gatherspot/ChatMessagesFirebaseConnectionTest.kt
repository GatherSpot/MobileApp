package com.github.se.gatherspot

import com.github.se.gatherspot.model.chat.ChatMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ChatMessagesFirebaseConnectionTest {
  val chatMessagesFirebase = ChatMessagesFirebaseConnection()
  private val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

  @Test
  fun testAddAndFetchMessage() = runBlocking {
    val eventId = UUID.randomUUID().toString()
    val message =
        ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = UUID.randomUUID().toString(),
            eventId = eventId,
            message = "Hello, world!",
            timestamp = LocalDateTime.now())
    chatMessagesFirebase.addMessage(eventId, message)
    val messages = chatMessagesFirebase.fetchMessages(eventId, 1)
    assertFalse(messages.isEmpty())
    assertEquals(message.senderId, messages[0].senderId)
    assertEquals(message.message, messages[0].message)

    assertEquals(
        message.timestamp.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
        messages[0].timestamp.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
    assertEquals(message.id, messages[0].id)
    assertEquals(message.eventId, messages[0].eventId)
    // cleanup
    chatMessagesFirebase.removeMessage(eventId, message.id)
  }

  @Test
  fun testDelete() = runBlocking {
    val eventId = UUID.randomUUID().toString()
    val message =
        ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = UUID.randomUUID().toString(),
            eventId = eventId,
            message = "Hello, world!",
            timestamp = LocalDateTime.now())
    chatMessagesFirebase.addMessage(eventId, message)
    val messages = chatMessagesFirebase.fetchMessages(eventId, 1)
    assertFalse(messages.isEmpty())
    chatMessagesFirebase.removeMessage(eventId, message.id)
    val messagesAfterDelete = chatMessagesFirebase.fetchMessages(eventId, 1)
    assertEquals(0, messagesAfterDelete.size)
  }
}
