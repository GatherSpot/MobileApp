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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun SignUp(nav: NavigationActions) {

  var username by remember { mutableStateOf("") }
  var isUsernameValid by remember { mutableStateOf(true) }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordDisplayed by remember { mutableStateOf(false) }
  var isPasswordValid by remember { mutableStateOf(false) }
  var signUpFailed by remember { mutableStateOf(false) }
  var isClicked by remember { mutableStateOf(false) }
  var verifEmailSent by remember { mutableStateOf(false) }
  val t = remember { mutableStateOf("") }
  val ProfileFirebaseConnection = ProfileFirebaseConnection()

  LaunchedEffect(isClicked) {
    if (isClicked) {
      try {
        withContext(Dispatchers.IO) {
          val success = checkCredentials(email, password, t)
          if (success) {
            FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().await()
            verifEmailSent = true
          } else {
            signUpFailed = true
            isClicked = false
          }
        }
      } catch (e: Exception) {
        Log.d(TAG, e.toString())
      }
    }
  }

  LaunchedEffect(key1 = username) {
    ProfileFirebaseConnection.ifUsernameExists(username) { result -> isUsernameValid = !result }
  }

  Box(modifier = Modifier.fillMaxSize().background(Color.White).testTag("signUpScreen")) {
    Column(
        modifier = Modifier.padding(vertical = 30.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(60.dp, Alignment.Top),
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

            Spacer(modifier = Modifier.width(90.dp))

            Text("Sign Up", fontSize = 30.sp, lineHeight = 32.sp, color = Color.Black)
          }

      Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(5.dp)) {
            OutlinedTextField(
                value = username,
                onValueChange = { s -> username = s },
                label = { Text(text = "Username") },
                modifier = Modifier.testTag("user"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
            if (username.isEmpty()) {
              Text(text = "", color = Color.Red)
            } else if (isUsernameValid) {
              Text(text = "Username is valid", color = Color.Blue)
            } else {
              Text(
                  text = "Username is already in use",
                  color = Color.Red,
                  modifier = Modifier.testTag("badUsername"))
            }
          }

      Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(5.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = { s -> email = s },
                label = { Text(text = "Email") },
                modifier = Modifier.testTag("email"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

            if (email.isEmpty()) {
              Text(text = "", color = Color.Red)
            } else if (isEmailValid(email)) {
              Text(text = "Email is valid", color = Color.Blue)
            } else {
              Text(
                  text = "Email is not valid",
                  color = Color.Red,
                  modifier = Modifier.testTag("badEmail"))
            }
          }

      Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(5.dp)) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                  password = it
                  isPasswordValid = isPasswordValid(it)
                },
                label = { Text(text = "Password") },
                visualTransformation =
                    if (isPasswordDisplayed) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.testTag("password"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                  IconButton(
                      onClick = { isPasswordDisplayed = !isPasswordDisplayed },
                      content = {
                        if (isPasswordDisplayed) {
                          Icon(
                              painter = painterResource(id = R.drawable.hide),
                              contentDescription = "Toggle password visibility",
                              Modifier.size(30.dp))
                        } else {
                          Icon(
                              painter = painterResource(id = R.drawable.show),
                              contentDescription = "Toggle password visibility",
                              Modifier.size(30.dp))
                        }
                      })
                },
            )

            if (password.isEmpty()) {
              Text(text = "", color = Color.Red)
            } else if (isPasswordValid) {
              Text(text = "Password is valid", color = Color.Blue)
            } else {
              Text(
                  text = "Password is not valid",
                  color = Color.Red,
                  modifier = Modifier.testTag("badPassword"))
            }
          }

      Button(
          enabled = isEmailValid(email) and isUsernameValid and isPasswordValid,
          onClick = { isClicked = true },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
          modifier = Modifier.width(250.dp).testTag("validate").clickable { isClicked = true }) {
            Text("Sign Up", color = Color.White)
          }

      if (signUpFailed) {
        AlertDialog(
            modifier = Modifier.testTag("signUpFailed").clickable { signUpFailed = false },
            onDismissRequest = { signUpFailed = false },
            confirmButton = {},
            title = { Text("Signup Failed") },
            text = { Text(t.value) },
        )
      }

      if (verifEmailSent) {
        AlertDialog(
            modifier =
                Modifier.testTag("verification").clickable {
                  verifEmailSent = false
                  ProfileFirebaseConnection.add(
                      Profile(username, "", "", Firebase.auth.currentUser!!.uid, setOf()))
                  nav.controller.navigate("setup")
                },
            onDismissRequest = {
              verifEmailSent = false
              ProfileFirebaseConnection.add(Profile(username, "", "", "", setOf()))
              nav.controller.navigate("setup")
            },
            confirmButton = {},
            title = { Text("Verification Email Sent") },
            text = { Text("Please check your email to verify your account.") })
      }
    }
  }
}

fun isEmailValid(email: String): Boolean {
  if (email.isEmpty()) return false
  val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
  return email.matches(emailRegex.toRegex())
}

fun isPasswordValid(password: String): Boolean {
  if (password.isEmpty()) return false
  val passwordRegex = """^((?=\S*?[A-Z])(?=\S*?[a-z])(?=\S*?[0-9]).{6,})\S$"""
  return password.matches(passwordRegex.toRegex())
}

suspend fun checkCredentials(email: String, password: String, t: MutableState<String>): Boolean {
  val auth = FirebaseAuth.getInstance()

  return try {
    auth.createUserWithEmailAndPassword(email, password).await()
    true
  } catch (e: FirebaseAuthInvalidCredentialsException) {
    t.value = "Wrong credentials"
    false
  } catch (e: FirebaseAuthUserCollisionException) {
    t.value = "Email already in use"
    false
  }
}
