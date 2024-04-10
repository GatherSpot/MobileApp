package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventDataFormScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventDataFormScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EventDataFormScreen") }) {

  // Structural elements of the UI
  val eventScaffold: KNode = onNode { hasTestTag("EventDataFormScreen") }
  val topBar: KNode = onNode { hasTestTag("createEventTitle") }
  val backButton: KNode = onNode { hasTestTag("goBackButton") }
  val formColumn: KNode = onNode { hasTestTag("formColumn") }

  val eventTitle: KNode = onNode { hasTestTag("inputTitle") }
  val eventDescription: KNode = onNode { hasTestTag("inputDescription") }
  val eventStartDate: KNode = onNode { hasTestTag("inputStartDateEvent") }
  val eventEndDate: KNode = onNode { hasTestTag("inputEndDateEvent") }
  val eventTimeStart: KNode = onNode { hasTestTag("inputTimeStartEvent") }
  val eventTimeEnd: KNode = onNode { hasTestTag("inputTimeEndEvent") }
  val eventMinAttendees: KNode = onNode { hasTestTag("inputMinAttendees") }
  val eventMaxAttendees: KNode = onNode { hasTestTag("inputMaxAttendees") }
  val eventLocation: KNode = onNode { hasTestTag("inputLocation") }
  val eventInscriptionLimitDate: KNode = onNode { hasTestTag("inputInscriptionLimitDate") }
  val eventInscriptionLimitTime: KNode = onNode { hasTestTag("inputInscriptionLimitTime") }
  val eventSaveButton: KNode = onNode { hasTestTag("createEventButton") }

  val alertBox: KNode = onNode { hasTestTag("alertBox") }
  val alertBoxText: KNode = onNode { hasTestTag("errorMessage") }
  val alertBoxButton: KNode = onNode { hasTestTag("alertButton") }
}
