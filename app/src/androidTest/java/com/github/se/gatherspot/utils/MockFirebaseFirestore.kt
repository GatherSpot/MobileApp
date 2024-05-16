package com.github.se.gatherspot.utils

import com.google.firebase.firestore.FirebaseFirestore
import org.mockito.Mockito.mock

// Wrapper class
class FirebaseFirestoreWrapper(private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    fun collection(path: String) = firebaseFirestore.collection(path)
    // Add other methods as needed
}

// In your tests
val mockFirestoreWrapper = mock(FirebaseFirestoreWrapper::class.java)
`when`(mockFirestoreWrapper.collection(anyString())).thenReturn(mockCollectionReference)