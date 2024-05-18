package com.github.se.gatherspot.ui.topLevelDestinations

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.setUp.SetUpView
import com.github.se.gatherspot.ui.setUp.SetUpViewModel

@Preview
@Composable
fun SetUpProfilePreview() {
  val nav = rememberNavController()
  SetUpProfile(NavigationActions(nav))
}

@Composable
fun SetUpProfile(nav: NavigationActions) {
  val storeOwner = LocalViewModelStoreOwner.current!!
  val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner = storeOwner)
  SetUpView(viewModel, nav)
}
