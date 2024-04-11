package com.github.se.gatherspot.model

import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.model.Profile.Companion.defaultProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileTest{
    @Test
    fun defaultProfile_isCorrect() {
        val email = "email@mail.com"
        val user = User(UserFirebaseConnection.getUID(), email, "pass")
        val profile: Profile = defaultProfile(user)
        assertEquals(email, profile.userName)
        assertEquals(user.uid , profile.uid)

    }

}