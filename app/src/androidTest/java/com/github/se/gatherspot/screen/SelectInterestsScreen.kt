package com.github.se.gatherspot.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode


class SelectInterestsScreen (semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectInterestsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("selectInterestsScreen") }){

        val interestsList :KNode = child { hasTestTag("interestsList") }

}

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("loginScreen") }) {

    // Structural elements of the UI
    val loginButton: KNode = child { hasTestTag("loginButton") }
    val signUpButton: KNode = child { hasTestTag("signUpButton") }
}


