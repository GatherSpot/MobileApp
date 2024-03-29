package com.github.se.gatherspot.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.FirebaseConnection
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

@Composable
fun SignUp(nav: NavigationActions) {

  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordDisplayed by remember { mutableStateOf("") }
  var showDialog by remember { mutableStateOf(false) }
  var isClicked by remember { mutableStateOf(false) }
    var showDialogVerif by remember { mutableStateOf(false) }


  LaunchedEffect(isClicked) {
    if (isClicked) {
      val success = checkCredentials(email, password)
      if (success) {

        MainActivity.uid = FirebaseConnection.getUID()
        val newUser = User(MainActivity.uid, username, email, password, Profile(emptySet()))
        FirebaseConnection.addUser(newUser)
          FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().await()
          if (!FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
              showDialogVerif = true

          }
          else {
              nav.controller.navigate("setup")
          }
      } else {
        showDialog = true
        isClicked = false
      }
    }
  }
  Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
    Column(
        modifier = Modifier.padding(vertical = 80.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(70.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            Icon(
                painter = painterResource(R.drawable.backarrow),
                contentDescription = "",
                modifier =
                    Modifier.clickable { nav.controller.navigate("auth") }
                        .width(30.dp)
                        .height(30.dp))

            Spacer(modifier = Modifier.width(80.dp))

            Text("Sign Up", fontSize = 32.sp, lineHeight = 32.sp, color = Color.Black)
          }

      OutlinedTextField(
          value = username,
          onValueChange = { s -> username = s },
          label = { Text(text = "Username") })

      OutlinedTextField(
          value = email, onValueChange = { s -> email = s }, label = { Text(text = "Email") })

      OutlinedTextField(
          value = passwordDisplayed,
          onValueChange = { s ->
            password = s
            passwordDisplayed = "*".repeat(s.length)
          },
          label = { Text(text = "Password") })

      Button(
          enabled = isEmailValid(email) and password.isNotEmpty() and username.isNotEmpty(),
          onClick = { isClicked = true },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
          modifier = Modifier.width(250.dp)) {
            Text("Sign Up", color = Color.White)
          }

      if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            buttons = {},
            title = { Text("Login Failed") },
            text = { Text("Invalid email or password. Please try again.") })
      }
        if (showDialogVerif) {
        AlertDialog(
            onDismissRequest = {
                showDialogVerif = false
                nav.controller.navigate("auth")
                               },
            buttons = {},
            title = { Text("Verification Email Sent") },
            text = { Text("Please check your email to verify your account.") })
        }
    }
  }
}

fun isEmailValid(email: String): Boolean {
  val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
  return email.matches(emailRegex.toRegex())
}

suspend fun checkCredentials(email: String, password: String): Boolean {
  val auth = FirebaseAuth.getInstance()

  return try {
    auth.createUserWithEmailAndPassword(email, password).await()
    true
  } catch (e: Exception) {
    Log.d(TAG, e.toString())
    false
  }
}
