package com.github.se.gatherspot.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.se.gatherspot.R

class NavigationActions(val controller: NavHostController) {
  fun navigateTo(tld: TopLevelDestination) {
    controller.navigate(tld.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(controller.graph.findStartDestination().id) { saveState = true }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }

  fun goBack() {
    controller.navigate("home")
  }
}

object Route {
  const val EVENTS = "events"
  const val MAP = "map"
  const val CHAT = "chat"
  const val PROFILE = "profile"
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(Route.EVENTS, R.drawable.event, R.string.events),
        TopLevelDestination(Route.MAP, R.drawable.glob, R.string.map),
        TopLevelDestination(Route.CHAT, R.drawable.chat, R.string.chat),
        TopLevelDestination(Route.PROFILE, R.drawable.profile, R.string.profile))
