package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class FollowListScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FollowListScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("FollowListScreen") }) {
  val list: KNode = onNode { hasTestTag("FollowList") }
  val back: KNode = onNode { hasTestTag("goBackToView") }
  val title: KNode = onNode { hasTestTag("title") }
}
