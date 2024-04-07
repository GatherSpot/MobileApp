package com.github.se.gatherspot;

import org.junit.Test;

@RunWith(AndroidJUnit4::class)
public class SelectInterestsTest : TestCase() {
@get:Rule val composeTestRule = createAndroidComposeRule<SelectInterestsScreen>()
@get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

        @Test
        fun buttonsAreCorrectlyDisplayed() {
        ComposeScreen.onComposeScreen<SelectInterestsScreen>(composeTestRule) {
        // Test the UI elements
        for (interest in interestsList)
        }
                }

}


class LoginTest : TestCase() {
@get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

// The IntentsTestRule simply calls Intents.init() before the @Test block
// and Intents.release() after the @Test block is completed. IntentsTestRule
// is deprecated, but it was MUCH faster than using IntentsRule in our tests
@get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

@Test
  fun buttonsAreCorrectlyDisplayed() {
          ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
        // Test the UI elements
        loginButton {
        assertIsDisplayed()
        assertHasClickAction()
        }
        signUpButton {
        assertIsDisplayed()
        assertHasClickAction()
        }
        }
        }
