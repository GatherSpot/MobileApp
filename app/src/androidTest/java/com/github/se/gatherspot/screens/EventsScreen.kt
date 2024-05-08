package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.gatherspot.model.Interests
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
  val eventRow: KNode = onNode { hasTestTag("eventRow") }
  val dropdown: KNode = onNode { hasTestTag("dropdown") }
  val refresh: KNode = onNode { hasTestTag("refresh") }
  val fetchingText: KNode = onNode { hasTestTag("fetch") }
  val categories: List<KNode> =
      enumValues<Interests>().toList().map { i -> onNode { hasTestTag(i.toString()) } }
  val myEvents: KNode = onNode { hasTestTag("myEvents") }
  val registeredTo: KNode = onNode { hasTestTag("registeredTo") }
}
