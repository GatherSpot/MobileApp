package com.github.se.gatherspot.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.github.se.gatherspot.R
import com.github.se.gatherspot.ui.navigation.NavigationActions

@Composable
fun SignUp(nav: NavigationActions) {

  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordDisplayed by remember { mutableStateOf("") }

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
          enabled = isEmailValid(email) and password.isNotEmpty(),
          onClick = {},
          colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
          modifier = Modifier.width(250.dp)) {
            Text("Sign Up", color = Color.White)
          }
    }
  }
}

fun isEmailValid(email: String): Boolean {
  val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
  return email.matches(emailRegex.toRegex())
}
