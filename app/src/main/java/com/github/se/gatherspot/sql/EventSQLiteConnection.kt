package com.github.se.gatherspot.sql

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.se.gatherspot.model.event.Event

/** The DAO for the events */
@Dao
interface EventDao {

  /**
   * Get all events
   *
   * @return the list of events
   */
  @Query("SELECT * FROM event") fun getAll(): List<Event>?

  /**
   * You can use this one to sort between events you are registered to vs your events, etc.. simply
   * give the list of ids
   *
   * @param eventIds the list of ids
   * @return the list of events
   */
  @Query("SELECT * FROM event WHERE id IN (:eventIds)")
  fun getAll(eventIds: List<String>): List<Event>?

  /**
   * Get an event by its id
   *
   * @param id the id of the event
   * @return the event
   */
  @Query("SELECT * FROM event WHERE id = :id") fun get(id: String): Event?
  @Query("SELECT * FROM event WHERE organizerID = :id")
  fun getAllFromOrganizerId(id: String): List<Event>?

  @Query("SELECT * FROM event WHERE registeredUsers LIKE '%' || :id || '%'")
  fun getAllWhereIdIsRegistered(id: String): List<Event>?


  /**
   * Insert an event
   *
   * @param event the event
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(vararg event: Event)

  /**
   * Delete an event
   *
   * @param event the event
   */
  @Delete fun delete(event: Event)

  @Update fun update(event: Event)
}
