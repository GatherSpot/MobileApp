package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.chat.ChatMessage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class ChatMessagesFirebaseConnection {

  private val TAG = "ChatMessagesFirebase"
  val CHATS = "chatMessages"
  val MESSAGES = "messages"
  private val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

  /** Fetches messages for a given event. */
  open suspend fun fetchMessages(eventId: String, number: Long): MutableList<ChatMessage> {
    val messagesRef =
        FirebaseFirestore.getInstance()
            .collection(CHATS)
            .document(eventId)
            .collection(MESSAGES)
            .orderBy("timestamp")
            .limit(number)

    val querySnapshot = messagesRef.get().await()
    return querySnapshot.documents.mapNotNull { getFromDocument(it) }.toMutableList()
  }

  /** Adds a chat message under a specific event. */
  open fun addMessage(eventId: String, message: ChatMessage) {
    val messageMap =
        hashMapOf(
            "senderId" to message.senderId,
            "message" to message.message,
            "timestamp" to
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))

    FirebaseFirestore.getInstance()
        .collection(CHATS)
        .document(eventId)
        .collection(MESSAGES)
        .document(message.id)
        .set(messageMap)
        .addOnFailureListener { exception -> Log.w(TAG, "Error adding document", exception) }
  }

  /** Removes a chat message from a specific event. */
  open fun removeMessage(eventId: String, messageId: String) {
    FirebaseFirestore.getInstance()
        .collection(CHATS)
        .document(eventId)
        .collection(MESSAGES)
        .document(messageId)
        .delete()
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }

  /** Maps a Firestore document to a ChatMessage object. */
  fun getFromDocument(d: DocumentSnapshot): ChatMessage? {
    val messageId = d.id
    val senderId = d.getString("senderId") ?: return null
    val messageText = d.getString("message") ?: return null
    val timestamp =
        d.getString("timestamp")?.let {
          LocalDateTime.parse(it, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
        } ?: return null

    return ChatMessage(
        id = messageId,
        senderId = senderId,
        eventId = d.reference.parent.parent!!.id,
        message = messageText,
        timestamp = timestamp)
  }
}
