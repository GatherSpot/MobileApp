package com.github.se.gatherspot.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList

/** The DAO for the IdLists */
@Dao
interface IdListDao {

  /**
   * Insert an IdList
   *
   * @param idList the IdList
   */
  @Insert fun insert(vararg idList: IdList)

  /**
   * Get an IdList by its id
   *
   * @param collection the collection associated with the IdList
   * @param id the id
   * @return the IdList
   */
  @Query("SELECT * FROM id_list WHERE collection = :collection AND id = :id")
  fun get(collection: FirebaseCollection, id: String): IdList

  /**
   * Delete an IdList
   *
   * @param idList the IdList
   */
  @Delete fun delete(idList: IdList)
  /**
   * Add element to the IdList element list
   *
   * @param collection the collection associated with the IdList
   * @param id the id of the IdList
   * @param element the element to add
   */
  @Query(
      "UPDATE id_list SET elements = elements + :element WHERE collection = :collection AND id = :id")
  fun addElement(collection: FirebaseCollection, id: String, element: String)
}
