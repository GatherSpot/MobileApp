package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("EventsScreen") }) {
  val filterText: KNode = onNode { hasTestTag("filter") }
  val filterMenu: KNode = onNode { hasTestTag("filterMenu") }
  val createText: KNode = onNode { hasTestTag("create") }
  val createMenu: KNode = onNode { hasTestTag("createMenu") }
  val emptyText: KNode = onNode { hasTestTag("empty") }
  val eventsList: KNode = onNode { hasTestTag("eventsList") }
  val loadingText: KNode = onNode { hasTestTag("loading") }
  val fetchedText: KNode = onNode { hasTestTag("fetched") }
}
