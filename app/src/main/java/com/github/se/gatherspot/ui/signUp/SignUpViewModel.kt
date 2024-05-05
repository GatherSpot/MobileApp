package com.github.se.gatherspot.ui.signUp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SignUpViewModel() : ViewModel() {
  var userName = MutableLiveData("")
  var email = MutableLiveData("")
  var password = MutableLiveData("")
  var doesUserNameExist = MutableLiveData<Boolean>()
  var isEmailValid = MutableLiveData<Boolean>()
  var emailError = MutableLiveData("")
  var isPasswordValid = MutableLiveData<Boolean>()
  var isPassWordVisible = MutableLiveData(false)
  var isEverythingOk = MutableLiveData<Boolean>()
  var waitingEmailConfirmation = MutableLiveData(false)
  var isFinished = MutableLiveData(false)
  // FLOW FOR CONTEXT :
  // let user fill fields with some basic check (including duplicate names)
  // check if email is already in database when clicking sign in (can't do same as with username
  // from what I can tell)
  // tell confirmation email has been sent
  // wait for email confirmation and then add user to database and move to signup
  // TODO : check cases where it crashes, before moving to next screen (does profile exist, is it a
  // problem ? are we still moved to setup ?), or cases where we stop at email confirmation and then
  // sign in.
  private fun updateEverythingOk() {
    isEverythingOk.value =
        (doesUserNameExist.value == false &&
            isEmailValid.value == true &&
            isPasswordValid.value == true)
    println(
        "isEverythingOk: ${isEverythingOk.value} isPasswordValid: ${isPasswordValid.value} isEmailValid: ${isEmailValid.value} doesUsernameExist: ${doesUserNameExist.value}")
    println("isEverythingOk: ${isEverythingOk.value}")
  }

  fun navBack() {
    isFinished.value = true
  }

  fun updateUsername(string: String) {
    //TODO update this
    userName.value = string
    doesUserNameExist = ProfileFirebaseConnection().ifUsernameExists(string)
  }

  fun updateEmail(string: String) {
    email.value = string
    val emailRegex = Regex("^[A-Za-z](.*)(@)(.+)(\\.)(.+)")
    if (email.value!!.matches(emailRegex)) {
      isEmailValid.value = true
      emailError.value = ""
    } else {
      isEmailValid.value = false
      emailError.value = "Invalid Email"
    }
    updateEverythingOk()
  }

  fun updatePassword(string: String) {
    password.value = string
    val passRegex = """^((?=\S*?[A-Z])(?=\S*?[a-z])(?=\S*?[0-9]).{6,})\S$"""
    isPasswordValid.value = password.value!!.matches(passRegex.toRegex())
    updateEverythingOk()
  }

  fun flipPasswordVisibility() {
    isPassWordVisible.value = !(isPassWordVisible.value!!)
  }

  fun resendEmail() {
    Firebase.auth.currentUser!!.sendEmailVerification()
  }

  private val scope = CoroutineScope(Dispatchers.Main)
  private var job: Job? = null

  // periodically check if email is verified, then run finished when it is
  private fun checkEmailVerification() {
    job =
        scope.launch {
          while (isActive) {
            val user = Firebase.auth.currentUser
            user?.reload()?.addOnCompleteListener {
              if (user.isEmailVerified) {
                // Email is verified, you can now proceed
                finish()
              }
            }
            delay(3000) // delay for 3 seconds before checking again
          }
        }
  }

  private fun finish() {
    isFinished.value = true
    job?.cancel()
  }

  fun signUp() {
    Firebase.auth
        .createUserWithEmailAndPassword(email.value!!, password.value!!)
        .addOnSuccessListener() {
          println("authentified")
          Firebase.auth.currentUser!!.sendEmailVerification()
          isEverythingOk.value = false
          waitingEmailConfirmation.value = true
          Profile.add(userName.value!!, Firebase.auth.uid!!)
          checkEmailVerification()
        }
        .addOnFailureListener() {
          when (it) {
            is FirebaseAuthUserCollisionException -> {
              emailError.value = "Email already in use, try signing in!"
              isEmailValid.value = false
              updateEverythingOk()
            }
            else -> {
              Log.e("SignUpViewModel", "Error: ${it.message}")
            }
          }
        }
  }
}
