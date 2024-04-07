package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import com.github.se.gatherspot.model.Interests


class SelectInterestsScreen (semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectInterestsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("selectInterestsScreen") }){

        val interestsList : List<KNode> = Interests.entries.map({child { hasTestTag(it.toString()) }})

}




