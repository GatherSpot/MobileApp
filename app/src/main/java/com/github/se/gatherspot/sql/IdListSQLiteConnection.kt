package com.github.se.gatherspot.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList

@Dao
interface IdListDao {
  @Insert fun insert(vararg idList: IdList)

  @Query("SELECT * FROM id_list WHERE collection = :collection AND id = :id")
  fun get(collection: FirebaseCollection, id: String): IdList

  @Delete fun delete(idList: IdList)
}
