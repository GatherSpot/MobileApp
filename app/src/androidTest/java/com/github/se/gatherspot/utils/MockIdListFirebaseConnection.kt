package com.github.se.gatherspot.utils

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.IdList

class MockIdListFirebaseConnection : IdListFirebaseConnection() {
  override suspend fun fetchFromFirebase(
      id: String,
      category: FirebaseCollection,
      update: () -> Unit
  ): IdList? {
    return IdList(id, listOf("1", "2", "3"), category)
  }

  override fun saveToFirebase(idSet: IdList) {
    return
  }

  override fun add(
      id: String,
      tag: FirebaseCollection,
      elements: List<String>,
      onSuccess: () -> Unit
  ): MutableLiveData<IdList> {
    return MutableLiveData(IdList(id, listOf("1", "2", "3"), tag))
  }

  override fun deleteElement(
      id: String,
      category: FirebaseCollection,
      element: String,
      onSuccess: () -> Unit
  ) {
    return
  }

  override suspend fun fetch(
      id: String,
      category: FirebaseCollection,
      onSuccess: () -> Unit
  ): IdList {
    onSuccess()
    return IdList(id, listOf("1", "2", "3"), category)
  }

  override fun addElement(
      id: String,
      category: FirebaseCollection,
      element: String,
      onSuccess: () -> Unit
  ) {
    onSuccess()
  }

  override fun addTwoInSingleBatch(
      id1: String,
      category1: FirebaseCollection,
      element1: String,
      id2: String,
      category2: FirebaseCollection,
      element2: String,
      onSuccess: () -> Unit
  ) {
    onSuccess()
    return
  }

  override fun removeTwoInSingleBatch(
      id1: String,
      category1: FirebaseCollection,
      element1: String,
      id2: String,
      category2: FirebaseCollection,
      element2: String,
      onSuccess: () -> Unit
  ) {
    onSuccess()
    return
  }

  override fun exists(
      id: String,
      category: FirebaseCollection,
      element: String,
      onSuccess: () -> Unit
  ): MutableLiveData<Boolean> {
    return MutableLiveData(true)
  }

  override fun delete(id: String, category: FirebaseCollection, onSuccess: () -> Unit) {
    onSuccess()
    return
  }
}
