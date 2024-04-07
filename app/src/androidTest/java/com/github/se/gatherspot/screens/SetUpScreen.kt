package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.gatherspot.model.Category
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SetUpScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SetUpScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("setUpScreen") }) {

  // Structural elements of the UI
  val save: KNode = onNode { hasTestTag("saveButton") }
    val lazyColumn: KNode = onNode { hasTestTag("lazyColumn") }
    val allCategories: Set<KNode> = enumValues<Category>().map { category ->
        onNode { hasTestTag(category.toString()) }
    }.toSet()
    val emailText: KNode = onNode { hasTestTag("emailText") }
}
