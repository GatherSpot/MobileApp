package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.gatherspot.model.Interests
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SelectInterestsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectInterestsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("selectInterestsScreen") }) {

  val interestsList: List<KNode> = Interests.entries.map({ child { hasTestTag(it.toString()) } })
}
