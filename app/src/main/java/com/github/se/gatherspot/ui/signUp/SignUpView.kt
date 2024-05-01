package com.github.se.gatherspot.ui.signUp

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.R
import com.github.se.gatherspot.ui.navigation.NavigationActions

class SignUpView {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun SignUp(vm: SignUpViewModel) {
    val navBack = vm::navBack
    val userName = vm.userName.observeAsState()
    val email = vm.email.observeAsState()
    val isEmailValid = vm.isEmailValid.observeAsState()
    val emailError = vm.emailError.observeAsState()
    val password = vm.password.observeAsState()
    val isPasswordValid = vm.isPasswordValid.observeAsState()
    val isPasswordVisible = vm.isPassWordVisible.observeAsState()
    val doesUsernameExist = vm.doesUserNameExist.observeAsState()
    val updateUsername = vm::updateUsername
    val updateEmail = vm::updateEmail
    val updatePassword = vm::updatePassword
    val flipPassword = vm::flipPasswordVisibility
    val isEverythingOk = vm.isEverythingOk.observeAsState()
    val waitingEmailConfirmation = vm.waitingEmailConfirmation.observeAsState()
    val signUp = vm::signUp
    val resendEmail = vm::resendEmail
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
                  modifier = Modifier.clickable { navBack() }.width(30.dp).height(30.dp))

              Spacer(modifier = Modifier.width(90.dp))

              Text("Sign Up", fontSize = 30.sp, lineHeight = 32.sp, color = Color.Black)
            }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
              OutlinedTextField(
                  value = userName.value!!,
                  onValueChange = { updateUsername(it) },
                  label = { Text(text = "Username") },
                  modifier = Modifier.testTag("user"),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
              if (userName.value!!.isEmpty()) {
                Text(text = "", color = Color.Red)
              } else {
                if (doesUsernameExist.value == false) {
                  Text(text = "Username is valid", color = Color.Blue)
                }
                if (doesUsernameExist.value == true) {
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
                        value = email.value!!,
                        onValueChange = { updateEmail(it) },
                        label = { Text(text = "Email") },
                        modifier = Modifier.testTag("email"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

                    if (isEmailValid.value == true) {
                      Text(text = "Email is valid", color = Color.Blue)
                    }
                    if (isEmailValid.value == false) {
                      Text(
                          text = emailError.value!!,
                          color = Color.Red,
                          modifier = Modifier.testTag("badEmail"))
                    }
                  }

              Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    OutlinedTextField(
                        value = password.value!!,
                        onValueChange = { updatePassword(it) },
                        label = { Text(text = "Password") },
                        visualTransformation =
                            if (isPasswordVisible.value!!) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        modifier = Modifier.testTag("password"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                          IconButton(
                              onClick = { flipPassword() },
                              content = {
                                if (isPasswordVisible.value!!) {
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

                    if (isPasswordValid.value == true) {
                      Text(text = "Password is valid", color = Color.Blue)
                    }
                    if (isPasswordValid.value == false) {
                      Text(
                          text = "Password is not valid",
                          color = Color.Red,
                          modifier = Modifier.testTag("badPassword"))
                    }
                  }

              Button(
                  enabled = isEverythingOk.value == true,
                  onClick = { signUp() },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                  modifier = Modifier.width(250.dp).testTag("validate"),
                  content = { Text("Sign Up", color = Color.White) })
            }
      }
    }

    if (waitingEmailConfirmation.value == true) {
      AlertDialog(
          modifier = Modifier.testTag("verification"),
          onDismissRequest = {},
          confirmButton = { TextButton(onClick = { resendEmail() }) { Text("Resend Email") } },
          title = { Text("Verification Email Sent") },
          text = { Text("Please check your email to verify your account before continuing.") })
    }
  }

  @Preview
  @Composable
  fun SignUpPreview() {
    val navController = rememberNavController()
    SignUp(SignUpViewModel(NavigationActions(navController)))
  }
}
// convocation et ordre du jour + lien doc ordre du jour.
