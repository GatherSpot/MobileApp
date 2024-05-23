package com.github.se.gatherspot.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.se.gatherspot.model.event.Event

@Dao
interface EventDao {
  @Query("SELECT * FROM event") fun getAll(): List<Event>

  /**
   * You can use this one to sort between events you are registered to vs your events, etc.. simply
   * give the list of ids
   */
  @Query("SELECT * FROM event WHERE id IN (:eventIds)")
  fun getAll(eventIds: List<String>): List<Event>

  @Query("SELECT * FROM event WHERE organizerID = :id")
  fun getAllFromOrganizerId(id: String): List<Event>

  @Query("SELECT * FROM event WHERE registeredUsers LIKE '%' || :id || '%'")
  fun getAllWhereIdIsRegistered(id: String): List<Event>

  @Query("SELECT * FROM event WHERE id = :id") fun get(id: String): Event

  @Insert fun insert(vararg event: Event)

  @Delete fun delete(event: Event)
}
