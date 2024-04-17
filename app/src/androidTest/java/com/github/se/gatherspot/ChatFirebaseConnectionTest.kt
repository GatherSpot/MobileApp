package com.github.se.gatherspot

import com.github.se.gatherspot.model.chat.Chat
import com.github.se.gatherspot.model.chat.Message
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import kotlin.time.Duration

class ChatFirebaseConnectionTest {

    val ChatFirebaseConnection = ChatFirebaseConnection()
  @Test
  fun testAddAndFetchChat() = runTest {
    val chatID = ChatFirebaseConnection.getNewID()
    val chat =
        Chat(
            chatID,
            listOf("1", "2"),
            "",
            listOf(
                Message(
                    "0",
                    "1",
                    "Hello",
                    Timestamp.now(),
                    false
                )
            )
        )


    ChatFirebaseConnection.add(chat)
    var resultChat: Chat? = null
    async { resultChat = ChatFirebaseConnection.fetch(chatID) as Chat? }.await()
    Assert.assertNotNull(resultChat)
    Assert.assertEquals(resultChat!!.id, chatID)
    Assert.assertEquals(resultChat!!.eventID, chat.eventID)
      for (i in chat.messages.indices) {
          Assert.assertEquals(resultChat!!.messages[i].id, chat.messages[i].id)
          Assert.assertEquals(resultChat!!.messages[i].senderID, chat.messages[i].senderID)
          Assert.assertEquals(resultChat!!.messages[i].content, chat.messages[i].content)
          Assert.assertEquals(resultChat!!.messages[i].timestamp, chat.messages[i].timestamp)
          Assert.assertEquals(resultChat!!.messages[i].read, chat.messages[i].read)
      }
    Assert.assertEquals(resultChat!!.messages[0].id, chat.messages[0].id)
    Assert.assertEquals(resultChat!!.peopleIDs, chat.peopleIDs)
  }

  @Test
  fun fetchReturnsNull() = runTest {
    // Supposing that id will never equal nonexistent
    val chat = ChatFirebaseConnection.fetch("nonexistent")
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
          listOfChats2.forEach { chat2 -> Assert.assertNotEquals(chat1.id, chat2.id) }
        }

        ChatFirebaseConnection.offset = null
      }
}
