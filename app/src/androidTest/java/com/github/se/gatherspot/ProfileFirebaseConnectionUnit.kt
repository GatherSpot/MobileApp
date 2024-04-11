package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.addDefaultProfile
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.addProfile
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.deleteProfile
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.fetchProfile
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.userNameIsAvailable
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.resume

class ProfileFirebaseConnectionUnit {


    @Test
    fun testAddProfileAndDelete() = runTest{
        val uid = UserFirebaseConnection.getUID()
        val userName = "valid"
        ProfileFirebaseConnection.addProfile(Profile(uid,userName,"","",null))
        var profileFetched: Profile? = null
        async { profileFetched = ProfileFirebaseConnection.fetchProfile(uid) }.await()
        assertNotNull(profileFetched)
        assertEquals(uid, profileFetched!!.uid)
        assertEquals(userName, profileFetched!!.userName)

        ProfileFirebaseConnection.deleteProfile(uid)
    }

    @Test
    fun testAddDefaultProfileAndDelete() = runTest {
        val uid = UserFirebaseConnection.getUID()
        val email = "random"
        val password = "random"
        val user = User(uid, email, password)
        ProfileFirebaseConnection.addDefaultProfile(user)
        var profileFetched: Profile? = null
        async { profileFetched = ProfileFirebaseConnection.fetchProfile(uid) }.await()
        assertNotNull(profileFetched)
        assertEquals(uid, profileFetched!!.uid)
        assertEquals(email, profileFetched!!.userName)

        ProfileFirebaseConnection.deleteProfile(uid)
        async { profileFetched = ProfileFirebaseConnection.fetchProfile(uid) }.await()
        assertNull(profileFetched)
    }

    @Test
    fun editUserName_isCorrect() = runTest {
        val uid = UserFirebaseConnection.getUID()
        val email = "johnwick@gmail.com"
        ProfileFirebaseConnection.addDefaultProfile(User(uid,email,""))
        async {ProfileFirebaseConnection.editUserName(uid, "")}.await()
        assertEquals(email, fetchProfile(uid)?.userName ?: null)
        val validUserName = "available"
        async {ProfileFirebaseConnection.editUserName(uid, validUserName)}.await()
        assertEquals(validUserName, fetchProfile(uid)?.userName ?: null)
        deleteProfile(uid)

    }

    @Test
    fun userNameIsAvailable_isCorrect() = runTest{
        assertTrue(ProfileFirebaseConnection.userNameIsAvailable("unassigned"))
        assertFalse(ProfileFirebaseConnection.userNameIsAvailable("johnwick@gmail.com"))
    }

    @Test
    fun testing() = runTest{
        var boolean = false
        Firebase.firestore
            .collection(ProfileFirebaseConnection.PROFILES)
            .document("NvD0t5fH0hcUZjzDGxD")
            .get()
            .addOnSuccessListener { doc ->
                boolean = true
            }
            .addOnFailureListener { exception ->

            }
        assert(boolean)
    }

}
