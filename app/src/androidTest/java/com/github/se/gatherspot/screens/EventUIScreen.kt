package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventUIScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventUIScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EventUIScreen") }) {
  val eventScaffold: KNode = onNode { hasTestTag("EventUIScreen") }
  val topBar: KNode = onNode { hasTestTag("topBar") }
  val deleteButton: KNode = onNode { hasTestTag("deleteEventButton") }
  val editEventButton: KNode = onNode { hasTestTag("editEventButton") }
  val calendarButton: KNode = onNode { hasTestTag("exportToCalendarButton") }
  val backButton: KNode = onNode { hasTestTag("goBackButton") }
  val formColumn: KNode = onNode { hasTestTag("eventColumn") }
  val image: KNode = onNode { hasTestTag("image") }
  val profileIndicator: KNode = onNode { hasTestTag("profileIndicator") }
  val userName: KNode = onNode { hasTestTag("userName") }
  val description: KNode = onNode { hasTestTag("eventDescription") }
  val attendeesInfoTitle = onNode { hasTestTag("attendeesInfoTitle") }
  val attendeesInfo = onNode { hasTestTag("attendeesInfo") }
  val categories = onNode { hasTestTag("categoriesRow") }
  val mapView = onNode { hasTestTag("mapView") }
  val eventDatesTimes = onNode { hasTestTag("eventDatesTimes") }
  val inscriptionLimitTitle = onNode { hasTestTag("inscriptionLimitTitle") }
  val inscriptionLimitDateAndTime = onNode { hasTestTag("inscriptionLimitDateAndTime") }
  val registerButton = onNode { hasTestTag("registerButton") }
  val attendButton = onNode { hasTestTag("attendButton") }
  val alertBox = onNode { hasTestTag("alertBox") }
  val okButton = onNode { hasTestTag("okButton") }
  val cancelButton = onNode { hasTestTag("cancelButton") }
  val starRow = onNode { hasTestTag("starRow") }
  val starIcon_1 = onNode { hasTestTag("starIcon_1") }
  val eventRating = onNode { hasTestTag("eventRating") }
  val bottomSpacer = onNode { hasTestTag("bottomSpacer") }
  val checkin = onNode { hasTestTag("checkin") }
  val verifyCheckin = onNode { hasTestTag("verifyCheckin") }
}
