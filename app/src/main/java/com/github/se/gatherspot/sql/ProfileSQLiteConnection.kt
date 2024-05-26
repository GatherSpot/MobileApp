package com.github.se.gatherspot.sql

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.se.gatherspot.model.Profile

@Dao
interface ProfileDao {
  @Insert fun insert(profile: Profile)

  @Update fun update(profile: Profile)

  @Query("SELECT * FROM profile WHERE id = :id") fun get(id: String): LiveData<Profile?>

  @Query("UPDATE profile SET image = :image WHERE id = :id")
  fun updateImage(id: String, image: String)
}
