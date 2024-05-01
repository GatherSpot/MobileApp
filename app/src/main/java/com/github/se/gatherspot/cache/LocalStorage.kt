package com.github.se.gatherspot.cache

import android.content.Context
import android.graphics.Bitmap
import com.github.se.gatherspot.model.event.DraftEvent
import com.github.se.gatherspot.model.utils.ImageBitmapSerializer
import com.google.gson.GsonBuilder
import java.io.InputStreamReader

class LocalStorage(private val context: Context) {
  private val gson = GsonBuilder()
    .registerTypeAdapter(Bitmap::class.java, ImageBitmapSerializer())
    .create()

  fun loadDraftEvent(): DraftEvent? {
    return try {
      val inputStreamReader = InputStreamReader(context.openFileInput("draftEvent.json"))
      val draftEventJson = inputStreamReader.readText()
      gson.fromJson(draftEventJson, DraftEvent::class.java)
    } catch (e: Exception) {
      null
    }
  }

  fun storeDraftEvent(draftEvent: DraftEvent) {
    try {
      val draftEventJson = gson.toJson(draftEvent)
      context.openFileOutput("draftEvent.json", Context.MODE_PRIVATE).use {
        it.write(draftEventJson.toByteArray())
      }
    } catch (e: Exception) {
      throw Exception("Error storing draft event to local storage")
    }
  }

  fun deleteDraftEvent() {
    try {
      context.deleteFile("draftEvent.json")
    } catch (e: Exception) {
      throw Exception("Error deleting draft event from local storage")
    }
  }
}
