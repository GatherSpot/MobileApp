package com.github.se.gatherspot.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.se.gatherspot.R.*

/** Wrapper for navigating between screens. */
open class NavigationActions(val controller: NavHostController) {

  /**
   * Navigate to a top-level destination.
   *
   * @param tld The top-level destination
   */
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

  /** Navigate to the previous screen. */
  fun goBack() {
    controller.popBackStack()
  }
}

/**
 * Route constants.
 *
 * @property EVENTS The events route
 * @property MAP The map route
 * @property CHATS The chats route
 * @property PROFILE The profile route
 */
object Route {
  const val EVENTS = "events"
  const val MAP = "map"
  const val CHATS = "chats"
  const val PROFILE = "profile"
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(Route.EVENTS, drawable.event, string.events),
        TopLevelDestination(Route.MAP, drawable.map_black, string.map),
        TopLevelDestination(Route.CHATS, drawable.chat, string.chats),
        TopLevelDestination(Route.PROFILE, drawable.profile, string.profile))
