package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.chat.Chat
import com.github.se.gatherspot.model.chat.Message
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class ChatFirebaseConnection {

  companion object {

    const val TAG = "ChatFirebase"
    const val CHATS = "chats" // Collection name for chats
    var offset: DocumentSnapshot? = null

    fun getNewChatID(): String {
      return FirebaseDatabase.getInstance()
          .getReference()
          .child(UserFirebaseConnection.USERS)
          .push()
          .key!!
    }

    suspend fun fetchNextChats(number: Long): MutableList<Chat> {

      val currentUser = Firebase.auth.currentUser

      val querySnapshot: QuerySnapshot =
          if (offset == null) {
            Firebase.firestore.collection(CHATS).orderBy("chatID").limit(number).get().await()
          } else {
            Firebase.firestore
                .collection(EventFirebaseConnection.EVENTS)
                .orderBy("chatID")
                .startAfter(EventFirebaseConnection.offset!!.get("chatID"))
                .limit(number)
                .get()
                .await()
          }

      if (querySnapshot.documents.isNotEmpty()) {
        offset = querySnapshot.documents.last()
      }

      val listOfMaps = querySnapshot.documents.map { it.data!! }
      val listOfChats = mutableListOf<Chat>()

      listOfMaps.forEach { map ->
        val uid = map["chatID"] as String
        val chat = fetchChat(uid)
        chat?.let { listOfChats.add(it) }
      }

      return listOfChats
    }

    suspend fun fetchChat(eventID: String): Chat? = suspendCancellableCoroutine { continuation ->
      Firebase.firestore
          .collection(CHATS)
          .document(eventID)
          .get()
          .addOnSuccessListener { result ->
            val chat = mapDocToChat(result)
            continuation.resume(chat)
          }
          .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
    }

    fun mapDocToChat(document: DocumentSnapshot): Chat? {
      if (document.getString("chatID") == null) {
        return null
      }
      val chatID = document.getString("chatID")!!
      val people = document.get("people") as List<*>
      val event = document.get("event") as Event
      val messages = document.get("messages") as List<*>

      return Chat(
          chatID = chatID,
          people = people.map { it as Profile },
          event = event,
          messages = messages.map { it as Message })
    }

    fun addChat(chat: Chat) {
      val userMap: HashMap<String, Any?> =
          hashMapOf(
              "chatID" to chat.chatID,
              "people" to chat.people,
              "event" to chat.event,
              "messages" to chat.messages)

      chat.chatID.let {
        Firebase.firestore
            .collection(CHATS)
            .document(it)
            .set(userMap)
            .addOnSuccessListener { Log.d(UserFirebaseConnection.TAG, "Chat successfully added!") }
            .addOnFailureListener { e ->
              Log.w(UserFirebaseConnection.TAG, "Error creating chat", e)
            }
      }
    }

    fun deleteChat(chatID: String) {
      Log.d(UserFirebaseConnection.TAG, "Deleting chat with id: $chatID")
      Firebase.firestore
          .collection(UserFirebaseConnection.USERS)
          .document(chatID)
          .delete()
          .addOnFailureListener { exception ->
            Log.e(UserFirebaseConnection.TAG, "Error deleting chat", exception)
          }
    }

    fun addMessage(message: Message, chatID: String) {
      val userMap: HashMap<String, Any?> =
          hashMapOf(
              "messageID" to message.messageID,
              "senderID" to message.senderID,
              "content" to message.content,
              "timestamp" to message.timestamp,
              "read" to message.read)

      Firebase.firestore
          .collection(CHATS)
          .document(chatID)
          .collection("messages")
          .document(message.messageID)
          .set(userMap)
          .addOnSuccessListener { Log.d(UserFirebaseConnection.TAG, "Message successfully added!") }
          .addOnFailureListener { e ->
            Log.w(UserFirebaseConnection.TAG, "Error creating message", e)
          }
    }

    fun deleteMessage(messageID: String, chatID: String) {
      Log.d(UserFirebaseConnection.TAG, "Deleting message with id: $messageID")
      Firebase.firestore
          .collection(UserFirebaseConnection.USERS)
          .document(chatID)
          .collection("messages")
          .document(messageID)
          .delete()
          .addOnFailureListener { exception ->
            Log.e(UserFirebaseConnection.TAG, "Error deleting message", exception)
          }
    }
  }
}
