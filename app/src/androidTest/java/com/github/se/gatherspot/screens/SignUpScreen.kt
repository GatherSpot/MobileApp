package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SignUpScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SignUpScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("signUpScreen") }) {

  // Structural elements of the UI
  val usernameField: KNode = child { hasTestTag("user") }
  val emailField: KNode = child { hasTestTag("email") }
  val passwordField: KNode = child { hasTestTag("password") }
  val button: KNode = child { hasTestTag("validate") }
  val dialog: KNode = child { hasTestTag("signUpFailed") }
  val verifDialog: KNode = child { hasTestTag("verification") }
  val ok: KNode = child { hasTestTag("okButton") }
}
