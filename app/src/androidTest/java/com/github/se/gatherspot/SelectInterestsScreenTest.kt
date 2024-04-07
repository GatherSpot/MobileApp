package com.github.se.gatherspot

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.github.se.gatherspot.screens.SelectInterestsScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SelectInterestsTest : TestCase(){
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun buttonsAreDisplayed(){
        ComposeScreen.onComposeScreen<SelectInterestsScreen>(composeTestRule){
            for (interest in interestsList){
                interest{
                    assertIsDisplayed()
                }
            }
        }
    }
}