package com.github.se.gatherspot.ui

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.github.se.gatherspot.R
import com.github.se.gatherspot.ui.navigation.NavigationActions

@Composable
fun LogIn(nav: NavigationActions, launcher: ActivityResultLauncher<Intent>) {
  // Manages logging into accounts and signing up
  // Gmail / Tequila
  val s = "Welcome to the GatherSpot !"

  val infiniteTransition = rememberInfiniteTransition(label = "title")
  val range by
      infiniteTransition.animateValue(
          initialValue = 0,
          targetValue = s.length + 1,
          typeConverter = Int.VectorConverter,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(durationMillis = 3500, delayMillis = 500),
                  repeatMode = RepeatMode.Reverse),
          label = "index")

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
      modifier = Modifier.background(color = Color.LightGray).testTag("loginScreen")) {
        Text(s.substring(0, range.coerceAtMost(s.length)), fontSize = 24.sp)
        Image(
            painter = painterResource(R.drawable.community),
            contentDescription = "",
            modifier = Modifier.width(200.dp).height(200.dp))
        Button(
            onClick = {
              val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
              val signInIntent =
                  AuthUI.getInstance()
                      .createSignInIntentBuilder()
                      .setAvailableProviders(providers)
                      .setIsSmartLockEnabled(false)
                      .build()
              launcher.launch(signInIntent)
            },
            contentPadding = PaddingValues(start = 1.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.width(250.dp).height(40.dp).testTag("loginButton")) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center,
              ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_google),
                    contentDescription = "Google Logo")
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.box),
                    contentDescription = "Email",
                    modifier = Modifier.width(30.dp).height(30.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Sign in with Gmail/Email", color = Color.White)
              }
            }

        Button(
            onClick = { nav.controller.navigate("signup") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.width(190.dp).height(40.dp).testTag("signUpButton")) {
              Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_google),
                    contentDescription = "Google Logo")
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.box),
                    contentDescription = "Email",
                    modifier = Modifier.width(30.dp).height(30.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Sign up", color = Color.White)
              }
            }
      }
}
