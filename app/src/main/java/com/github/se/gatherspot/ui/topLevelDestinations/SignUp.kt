package com.github.se.gatherspot.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.signUp.SignUpView
import com.github.se.gatherspot.ui.signUp.SignUpViewModel

/**
 * Composable for the sign up screen.
 *
 * @param nav The navigation actions
 */
@Composable
fun SignUp(nav: NavigationActions) {
  val storeOwner = LocalViewModelStoreOwner.current!!
  val viewModel = viewModel<SignUpViewModel>(viewModelStoreOwner = storeOwner)
  SignUpView().SignUp(viewModel, nav)
}
