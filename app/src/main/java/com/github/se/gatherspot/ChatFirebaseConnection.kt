package com.github.se.gatherspot

import com.github.se.gatherspot.model.Chat
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class ChatFirebaseConnection {

  companion object {

    const val CHATS = "chats" // Collection name for chats
    var offset: DocumentSnapshot? = null

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

      return Chat(chatID = chatID, people = people.map { it as Profile }, event = event)
    }
  }
}
