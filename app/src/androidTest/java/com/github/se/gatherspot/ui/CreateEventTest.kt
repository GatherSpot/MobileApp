/// *
// package com.github.se.gatherspot
//
// import androidx.compose.ui.test.junit4.createComposeRule
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import org.junit.runner.RunWith
// import com.kaspersky.kaspresso.kaspresso.Kaspresso
// import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
// import io.mockk.Called
// import io.mockk.confirmVerified
// import io.mockk.impl.annotations.RelaxedMockK
// import io.mockk.junit4.MockKRule
// import io.mockk.verify
// import org.junit.Assert.*
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.runner.RunWith
// import com.github.se.gatherspot.model.EventViewModel
// import com.github.se.gatherspot.ui.CreateEvent
// import com.kaspersky.components.composesupport.config.withComposeSupport
// import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
//
//
// @RunWith(AndroidJUnit4::class)
// class CreateToDoTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
//
//    @get:Rule val composeTestRule = createComposeRule()
//
//    @get:Rule val mockkRule = MockKRule(this)
//
//    @RelaxedMockK lateinit var mockNavActions: NavigationActions
//
//    @Before
//    fun testSetup() {
//        val vm = EventViewModel()
//        composeTestRule.setContent { CreateEvent(vm, mockNavActions) }
//    }
//
//    @Test
//    fun goBackButtonTriggersBackNavigation() = run {
//        onComposeScreen<CreateEventScreen>(composeTestRule) {
//            goBackButton {
//                assertIsDisplayed()
//                assertIsEnabled()
//
//                performClick()
//            }
//        }
//
//        verify { mockNavActions.goBack() }
//        confirmVerified(mockNavActions)
//    }
//
//    // Add more tests here...
// }*/
