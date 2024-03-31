package com.github.se.gatherspot.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.data.Profile
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//NOTE: For ui tests to work, and to make app accessible, please ADD CONTENT DESCRIPTION TO EVERY COMPOSE NODE
// adding a text is not enough, as we will probably change theses when internationalizing texts
@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {
    //to start the main activity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    //Note : to understand / help you to do your own tests, use this cheatsheet :
    // https://developer.android.com/develop/ui/compose/testing-cheatsheet
    @Test
    fun profileUITest() {
        //click on the login button TODO: when actual login page will be implemented, we will need to inject a dummyLogin or this will not work
        composeTestRule.onNodeWithContentDescription("login").performClick()
        //click on the profile button
        composeTestRule.onNodeWithText("Profile").performClick()
        //check if everything is there
        composeTestRule.onNodeWithContentDescription("username").assertExists("username field not found")
        composeTestRule.onNodeWithContentDescription("bio").assertExists("bio field not found")
        composeTestRule.onNodeWithContentDescription("edit").assertExists("edit button not found")
        //check buttons that should not be there yet are not here yet
        composeTestRule.onNodeWithContentDescription("cancel").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("save").assertDoesNotExist()
        //press edit button and check the buttons change accordingly
        composeTestRule.onNodeWithContentDescription("edit").performClick()
        composeTestRule.onNodeWithContentDescription("cancel").assertExists()
        composeTestRule.onNodeWithContentDescription("save").assertExists()
        composeTestRule.onNodeWithContentDescription("edit").assertDoesNotExist()
        //modify text, press cancel, and verify it didn't change.
        val defaultProfile = Profile()
        composeTestRule.onNodeWithContentDescription("username").performTextReplacement("Alex")
        composeTestRule.onNodeWithContentDescription("username").assert(hasText("Alex"))
        composeTestRule.onNodeWithContentDescription("cancel").performClick()
        composeTestRule.onNodeWithContentDescription("username").assert(hasText("John Doe"))
        //modify text, press save and verify it did change.
        composeTestRule.onNodeWithContentDescription("edit").performClick()
        composeTestRule.onNodeWithContentDescription("bio").performTextReplacement("I like trains")
        composeTestRule.onNodeWithContentDescription("save").performClick()
        composeTestRule.onNodeWithContentDescription("bio").assert(hasText("I like trains"))
    }

}

