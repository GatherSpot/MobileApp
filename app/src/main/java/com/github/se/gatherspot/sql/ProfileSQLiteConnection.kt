package com.github.se.gatherspot.sql

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.se.gatherspot.model.Profile

@Dao
interface ProfileDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(profile: Profile)

  @Update fun update(profile: Profile)

  @Query("SELECT * FROM profile WHERE id = :id") fun get(id: String): Profile?

  @Query("UPDATE profile SET image = :image WHERE id = :id")
  fun updateImage(id: String, image: String)
}
