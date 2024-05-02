/*package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.chat.Chat
import com.github.se.gatherspot.model.chat.Message
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class ChatFirebaseConnection : FirebaseConnectionInterface<Chat> {

  override val COLLECTION: String = FirebaseCollection.CHATS.toString()
  override val TAG = "ChatFirebase"
  val CHATS = "chats" // Collection name for chats
  var offset: DocumentSnapshot? = null

  override fun getFromDocument(d: DocumentSnapshot): Chat? {
    if (d.getString("id") == null) {
      return null
    }
    val chatID = d.getString("id")!!
    val peopleIDs = d.get("peopleIDs") as List<*>
    val eventID = d.getString("eventID")!!
    val messages =
        (d.get("messages") as List<*>)
            .map { (it as HashMap<*, *>) }
            .map {
              Message(
                  it["id"] as String,
                  it["senderID"] as String,
                  it["content"] as String,
                  it["timestamp"] as Timestamp,
                  it["read"] as Boolean)
            }

    return Chat(
        id = chatID,
        peopleIDs = peopleIDs.map { it as String },
        eventID = eventID,
        messages = messages)
  }

  //  suspend fun fetchNextChats(number: Long): MutableList<Chat> {
  //    val userID = FirebaseAuth.getInstance().currentUser?.uid!!
  //    val listOfChatsIDs = ProfileFirebaseConnection().fetch(userID)?.chats!!
  //    val querySnapshot: QuerySnapshot =
  //        if (offset == null) {
  //          Firebase.firestore
  //              .collection(CHATS)
  //              .orderBy("id")
  //              .whereIn("id", listOfChatsIDs)
  //              .limit(number)
  //              .get()
  //              .await()
  //        } else {
  //          Firebase.firestore
  //              .collection(CHATS)
  //              .orderBy("id")
  //              .startAfter(offset!!.get("id"))
  //              .whereIn("id", listOfChatsIDs)
  //              .limit(number)
  //              .get()
  //              .await()
  //        }
  //
  //    if (querySnapshot.documents.isNotEmpty()) {
  //      offset = querySnapshot.documents.last()
  //    }
  //
  //    val listOfMaps = querySnapshot.documents.map { it.data!! }
  //    val listOfChats = mutableListOf<Chat>()
  //
  //    listOfMaps.forEach { map ->
  //      val uid = map["id"] as String
  //      val chat = fetch(uid)
  //      chat?.let { listOfChats.add(it) }
  //    }
  //
  //    return listOfChats
  //  }

  override fun add(element: Chat) {
    val userMap: HashMap<String, Any?> =
        hashMapOf(
            "id" to element.id,
            "peopleIDs" to element.peopleIDs,
            "eventID" to element.eventID,
            "messages" to element.messages)

    Firebase.firestore
        .collection(CHATS)
        .document(element.id)
        .set(userMap)
        .addOnSuccessListener { Log.d(TAG, "Chat successfully added!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error creating chat", e) }
  }

  fun addMessage(message: Message, chatID: String) {
    val userMap: HashMap<String, Any?> =
        hashMapOf(
            "id" to message.id,
            "senderID" to message.senderID,
            "content" to message.content,
            "timestamp" to message.timestamp,
            "read" to message.read)

    Firebase.firestore
        .collection(CHATS)
        .document(chatID)
        .collection("messages")
        .document(message.id)
        .set(userMap)
        .addOnSuccessListener { Log.d(TAG, "Message successfully added!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error creating message", e) }
  }

  fun deleteMessage(messageID: String, chatID: String) {
    Log.d(TAG, "Deleting message with id: $messageID")
    Firebase.firestore
        .collection(CHATS)
        .document(chatID)
        .collection("messages")
        .document("id")
        .delete()
        .addOnFailureListener { exception -> Log.e(TAG, "Error deleting message", exception) }
  }
}
*/
