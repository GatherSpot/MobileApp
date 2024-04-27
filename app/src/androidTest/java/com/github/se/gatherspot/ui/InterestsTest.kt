package com.github.se.gatherspot.ui

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.profile.InterestsView
import org.junit.Rule
import org.junit.Test

class InterestsTest {
  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun interestsTest() {
    val interests = MutableLiveData(Interests.addInterest(Interests.new(), Interests.FOOTBALL))
    composeTestRule.setContent {
      InterestsView().EditInterests(
          interestList = Interests.toList(),
          interests = interests.observeAsState(),
      ) {
        interests.value = Interests.flipInterest(interests.value!!, it)
      }
    }
    composeTestRule
        .onNodeWithTag("remove FOOTBALL")
        .assertExists("Remove FOOTBALL chip should exist")
    composeTestRule.onNodeWithTag("add FOOTBALL").assertDoesNotExist()
    composeTestRule.onNodeWithTag("remove FOOTBALL").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("remove FOOTBALL").assertDoesNotExist()
    composeTestRule.onNodeWithTag("add FOOTBALL").assertExists("Add FOOTBALL chip should exist")
    composeTestRule.onNodeWithTag("add BASKETBALL").assertExists()
    composeTestRule.onNodeWithTag("remove BASKETBALL").assertDoesNotExist()
    composeTestRule.onNodeWithTag("add BASKETBALL").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("remove BASKETBALL")
        .assertExists("Remove BASKETBALL chip should exist")
    composeTestRule.onNodeWithTag("add BASKETBALL").assertDoesNotExist()
  }
}
