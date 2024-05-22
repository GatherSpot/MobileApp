package com.github.se.gatherspot.cache

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.github.se.gatherspot.model.event.DraftEvent
import com.github.se.gatherspot.model.utils.ImageBitmapSerializer
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

/*
   * Local storage for draft events.
   *
   * @property context The context of the application
   * @property gson The Gson object for serialization

*/
class LocalStorage(private val context: Context) {
  private val gson =
      GsonBuilder().registerTypeAdapter(Bitmap::class.java, ImageBitmapSerializer()).create()

  /**
   * Load the draft event from local storage.
   *
   * @return The draft event or null if there is no draft event
   * @throws FileNotFoundException If the file is not found
   * @throws JsonSyntaxException If there is an error parsing the JSON
   * @throws IOException If there is an error reading the file
   */
  fun loadDraftEvent(): DraftEvent? {
    return try {
      val inputStreamReader = InputStreamReader(context.openFileInput("draftEvent.json"))
      val draftEventJson = inputStreamReader.readText()
      inputStreamReader.close()
      gson.fromJson(draftEventJson, DraftEvent::class.java)
    } catch (e: FileNotFoundException) {
      Log.e("LocalStorage", "File not found", e)
      null
    } catch (e: JsonSyntaxException) {
      Log.e("LocalStorage", "Error parsing JSON", e)
      null
    } catch (e: IOException) {
      Log.e("LocalStorage", "Error reading file", e)
      null
    }
  }

  /**
   * Store the draft event to local storage.
   *
   * @param draftEvent The draft event to store
   * @throws Exception If there is an error storing the draft event
   */
  fun storeDraftEvent(draftEvent: DraftEvent) {
    try {
      val draftEventJson = gson.toJson(draftEvent)
      context.openFileOutput("draftEvent.json", Context.MODE_PRIVATE).use {
        it.write(draftEventJson.toByteArray())
      }
    } catch (e: JsonSyntaxException) {
      throw Exception("Error parsing JSON")
    } catch (e: IOException) {
      throw Exception("Error writing file")
    }
  }

  /**
   * Delete the draft event from local storage.
   *
   * @return True if the draft event was deleted, false if there was no draft event to delete
   */
  fun deleteDraftEvent(): Boolean {
    return context.deleteFile("draftEvent.json")
  }
}
