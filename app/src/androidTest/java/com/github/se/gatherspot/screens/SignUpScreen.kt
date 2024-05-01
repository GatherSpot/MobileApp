package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SignUpScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SignUpScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("signUpScreen") }) {

  // Structural elements of the UI
  val all: KNode = onNode { hasTestTag("signUpScreen") }
  val usernameField: KNode = onNode { hasTestTag("user") }
  val emailField: KNode = onNode { hasTestTag("email") }
  val passwordField: KNode = onNode { hasTestTag("password") }
  val button: KNode = onNode { hasTestTag("validate") }
  val verifDialog: KNode = onNode { hasTestTag("verification") }
  val badUsername: KNode = onNode { hasTestTag("badUsername") }
  val badEmail: KNode = onNode { hasTestTag("badEmail") }
  val badPassword: KNode = onNode { hasTestTag("badPassword") }
}
