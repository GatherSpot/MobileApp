package com.github.se.gatherspot.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode


class SelectInterestsScreen (semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectInterestsScreen.LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("selectInterestsScreen") }){

        val interestsList = KNode { hasTestTag("interestsList") }

}


