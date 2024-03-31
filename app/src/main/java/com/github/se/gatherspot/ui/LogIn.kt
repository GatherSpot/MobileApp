package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.github.se.gatherspot.ui.navigation.NavigationActions

@Composable
fun LogIn(nav: NavigationActions) {
  // Manages logging into accounts and signing up
  // Gmail / Tequila
  Column {
    Button(
    onClick = { nav.controller.navigate("home") },
      modifier = Modifier.semantics { contentDescription = "login" }
  ) {} }
}
