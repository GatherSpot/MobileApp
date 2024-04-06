package com.github.se.gatherspot.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Note: every data object should inherit from DataType to make it consistent, and group data
// objects at the same place
// Warn : DO SANITIZATION AND USE SETTERS AND GETTERS

abstract class DataType<T> {
  /**
   * Converts a DataType object to a json string
   *
   * @return the json string
   */
  fun toJson(): String {
    // TODO: we currently have 2 json libraries imported, maybe we should use only one
    return Gson().toJson(this)
  }
  /**
   * Converts a json string to a DataType object
   *
   * @param json the json string to convert
   * @return the DataType object
   */
  fun fromJson(json: String): T {
    val type = object : TypeToken<Profile>() {}.type
    return Gson().fromJson(json, type)
  }
}
