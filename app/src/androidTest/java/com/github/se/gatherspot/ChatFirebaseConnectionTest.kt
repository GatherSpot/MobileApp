package com.github.se.gatherspot

import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.chat.Chat
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.time.Duration
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ChatFirebaseConnectionTest {

  @Test
  fun testAddAndFetchChat() = runTest {
    val chatID = ChatFirebaseConnection.getNewChatID()
    val chat =
        Chat(
            chatID,
            listOf(
                Profile("name1", "", "", "", emptySet()), Profile("name2", "", "", "", emptySet())),
            Event(
                "",
                "testEvent",
                "testEvent",
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                EventStatus.CREATED,
                emptySet(),
                Profile(),
                emptyList(),
                null,
                null,
                null),
            emptyList())

    ChatFirebaseConnection.addChat(chat)
    var resultChat: Chat? = null
    async { resultChat = ChatFirebaseConnection.fetchChat(chatID) }.await()
    Assert.assertNotNull(resultChat)
    Assert.assertEquals(resultChat!!.chatID, chatID)
    Assert.assertEquals(resultChat!!.event, chat.event)
    Assert.assertEquals(resultChat!!.messages, chat.messages)
    Assert.assertEquals(resultChat!!.people, chat.people)
  }

  @Test
  fun fetchReturnsNull() = runTest {
    // Supposing that id will never equal nonexistent
    val chat = ChatFirebaseConnection.fetchChat("nonexistent")
    Assert.assertEquals(chat, null)
  }

  @Test
  fun fetchNextReturnsDistinctChats() =
      runTest(timeout = Duration.parse("20s")) {
        val numberOfChats =
            Firebase.firestore.collection(ChatFirebaseConnection.CHATS).get().await().documents.size
        val round = 5
        val listOfChats1 = ChatFirebaseConnection.fetchNextChats(round.toLong())
        Assert.assertEquals(round, listOfChats1.size)
        val listOfChats2 = ChatFirebaseConnection.fetchNextChats(round.toLong())
        Assert.assertEquals(round, listOfChats2.size)

        listOfChats1.forEach { chat1 ->
          listOfChats2.forEach { chat2 -> Assert.assertNotEquals(chat1.chatID, chat2.chatID) }
        }

        EventFirebaseConnection.offset = null
      }
}
