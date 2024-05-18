package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class MapScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MapScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EditOwnProfileScreen") }) {

  val googleMap = onNode { hasTestTag("GoogleMap") }
  val topBar = onNode { hasTestTag("topBar") }
  val positionButton = onNode { hasTestTag("positionButton") }
  val registeredEvents = onNode { hasTestTag("registeredEvents") }
  val title = onNode { hasTestTag("title") }
}
