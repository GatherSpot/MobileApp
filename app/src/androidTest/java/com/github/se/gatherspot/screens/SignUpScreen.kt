package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the Login Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests, incl. grading! You can adapt the
 * test tags if necessary to suit your own implementation, but the class properties need to stay the
 * same.
 *
 * You can refer to Figma for the naming conventions.
 * https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1
 */
class SignUpScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SignUpScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("signUpScreen") }) {

  // Structural elements of the UI
  val usernameField: KNode = child { hasTestTag("user") }
  val emailField: KNode = child { hasTestTag("email") }
  val passwordField: KNode = child { hasTestTag("password") }
  val button: KNode = child { hasTestTag("validate") }
  val dialog: KNode = child { hasTestTag("signUpFailed") }
  val verifDialog: KNode = child { hasTestTag("verificationEmailSent") }
}
