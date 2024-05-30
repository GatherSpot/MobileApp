package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.gatherspot.model.Interests
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("EventsScreen") }) {
  // val filterText: KNode = onNode { hasTestTag("filter") }
  val filterMenu: KNode = onNode { hasTestTag("filterMenu") }
  val setFilterButton: KNode = onNode { hasTestTag("setFilterButton") }
  // val createText: KNode = onNode { hasTestTag("create") }
  val createMenu: KNode = onNode { hasTestTag("createMenu") }
  val emptyText: KNode = onNode { hasTestTag("empty") }
  val eventsList: KNode = onNode { hasTestTag("eventsList") }
  val eventItem: KNode = onNode { hasTestTag("eventItem") }
  val interestsDialog: KNode = onNode { hasTestTag("interestsDialog") }
  val refresh: KNode = onNode { hasTestTag("refresh") }
  val fetchingText: KNode = onNode { hasTestTag("fetch") }
  val categories: List<KNode> =
      enumValues<Interests>().toList().map { i -> onNode { hasTestTag(i.name) } }
  val upComing: KNode = onNode { hasTestTag("Planned") }
  val attended: KNode = onNode { hasTestTag("Attended") }
  val myEvents: KNode = onNode { hasTestTag("Mine") }
  val fromFollowed: KNode = onNode { hasTestTag("Follows") }
  val eventFeed: KNode = onNode { hasTestTag("Feed") }

  val removeFilter: KNode = onNode { hasTestTag("removeFilter") }
  val eventCreated: KNode = onNode { hasTestTag("Basketball Game") }
}
