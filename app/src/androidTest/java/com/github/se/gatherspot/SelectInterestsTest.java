package com.github.se.gatherspot;

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.screens.LoginScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
public class SelectInterestsTest : TestCase() {
@get:Rule val composeTestRule = createAndroidComposeRule<SelectInterestsScreen>()
@get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

@Test
    fun buttonsAreCorrectlyDisplayed() {
        ComposeScreen.onComposeScreen<SelectInterestsScreen>(composeTestRule) {
        // Test the UI elements
        for (interestButton in interestsList){
        interestButton {
            assertIsDisplayed()
            assertHasClickAction()
        }
        }
        }
                }

}


