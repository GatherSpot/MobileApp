package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateEventScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("CreateEventScreen") }) {

  // Structural elements of the UI
  val eventScaffold: KNode = child { hasTestTag("CreateEventScreen") }
  val topBar: KNode = child { hasTestTag("createEventTitle") }
  val backButton: KNode = child { hasTestTag("goBackButton") }

  val eventTitle: KNode = child { hasTestTag("inputTitle") }
  val eventDescription: KNode = child { hasTestTag("inputDescription") }
  val eventStartDate: KNode = child { hasTestTag("inputStartDateEvent") }
  val eventEndDate: KNode = child { hasTestTag("inputEndDateEvent") }
  val eventTimeStart: KNode = child { hasTestTag("inputTimeStartEvent") }
  val eventTimeEnd: KNode = child { hasTestTag("inputTimeEndEvent") }
  val eventLocation: KNode = child { hasTestTag("inputLocation") }
  val eventMaxAttendees: KNode = child { hasTestTag("inputMaxAttendees") }
  val eventMinAttendees: KNode = child { hasTestTag("inputMinAttendees") }
  val eventInscriptionLimitDate: KNode = child { hasTestTag("inputInscriptionLimitDate") }
  val eventInscriptionLimitTime: KNode = child { hasTestTag("inputInscriptionLimitTime") }
  val eventSaveButton: KNode = child { hasTestTag("createEventButton") }

  val alertBox: KNode = child { hasTestTag("alertBox") }
  val alertBoxText: KNode = child { hasTestTag("errorMessage") }
  val alertBoxButton: KNode = child { hasTestTag("alertButton") }
}
