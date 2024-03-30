package com.github.se.gatherspot.authentification

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.screens.LoginScreen
import com.github.se.gatherspot.screens.SignUpScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpTest : TestCase() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // The IntentsTestRule simply calls Intents.init() before the @Test block
    // and Intents.release() after the @Test block is completed. IntentsTestRule
    // is deprecated, but it was MUCH faster than using IntentsRule in our tests
    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun signUp() {
        ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule){
            signUpButton{
                performClick()
            }
        }

        ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
             usernameField{
                 performTextInput("GatherSpot")
             }
            emailField{
                performTextInput("gatherspot2024@gmail.com")
            }
            passwordField{
                performTextInput("GatherSpot,2024;")
            }

            button{
                performClick()
            }

        //    intended(toPackage("com.google.android.gms"))
        }
    }
}