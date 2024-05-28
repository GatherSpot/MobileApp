package com.github.se.gatherspot.ui.signUp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.sql.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** ViewModel for the sign up screen. */
class SignUpViewModel(
    private val db: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    private var dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
  var userName = MutableLiveData("")
  var userNameError = MutableLiveData("")
  var email = MutableLiveData("")
  var password = MutableLiveData("")
  var emailError = MutableLiveData("")
  var passwordError = MutableLiveData("")
  var isPassWordVisible = MutableLiveData(false)
  var isEverythingOk = MutableLiveData<Boolean>()
  var waitingEmailConfirmation = MutableLiveData(false)
  var isFinished = MutableLiveData(false)
  private var isUsernameUnique = false

  private fun updateEverythingOk() {
    isEverythingOk.postValue(
        (userNameError.value == "" &&
            emailError.value == "" &&
            passwordError.value == "" &&
            isUsernameUnique))
  }

  /** Update the username and check if it is unique. */
  fun updateUsername(string: String) {
    userName.value = string
    isUsernameUnique = false
    viewModelScope.launch(Dispatchers.IO) {
      Profile.checkUsername(string, null, userNameError)
      isUsernameUnique = userNameError.value == ""
    }
  }

  /** Update the email and check if it is valid. */
  fun updateEmail(string: String) {
    email.value = string
    val emailRegex = Regex("^[A-Za-z](.*)(@)(.+)(\\.)(.+)")
    emailError.value = if (email.value!!.matches(emailRegex)) "" else "Invalid Email"
    updateEverythingOk()
  }

  /** Update the password and check if it is valid. */
  fun updatePassword(string: String) {
    password.value = string
    val passRegex = """^((?=\S*?[A-Z])(?=\S*?[a-z])(?=\S*?[0-9]).{6,})\S$"""
    passwordError.value =
        if (password.value!!.matches(passRegex.toRegex())) "" else "Invalid Password"
    updateEverythingOk()
  }

  /** Flip the visibility of the password. */
  fun flipPasswordVisibility() {
    isPassWordVisible.value = !(isPassWordVisible.value!!)
  }

  /** Resend the verification email. */
  fun resendEmail() {
    Firebase.auth.currentUser!!.sendEmailVerification()
  }

  private var job: Job? = null

  /** Periodically check if email is verified, then run finish() when it is. */
  private fun checkEmailVerification() {
    job =
        scope.launch {
          while (isActive) {
            val user = Firebase.auth.currentUser
            user?.reload()?.addOnCompleteListener {
              if (user.isEmailVerified) {
                // Email is verified, you can now proceed
                emailIsVerified()
              }
            }
            delay(3000) // delay for 3 seconds before checking again
          }
        }
  }

  private fun emailIsVerified() {
    isFinished.value = true
    job?.cancel()
  }

  /** Sign up the user and create profile */
  fun signUp() {
    viewModelScope.launch(dispatcher) {
      try {
        Firebase.auth.createUserWithEmailAndPassword(email.value!!, password.value!!).await()
        async {
              Firebase.auth.currentUser!!.sendEmailVerification()
              ProfileFirebaseConnection()
                  .add(Profile(userName.value!!, "", "", Firebase.auth.uid!!, setOf()))
            }
            .await()
        isEverythingOk.postValue(false)
        waitingEmailConfirmation.postValue(true)
        checkEmailVerification()
      } catch (e: Exception) {
        if (e is FirebaseAuthUserCollisionException) {
          emailError.postValue("Email already in use, try signing in!")
          updateEverythingOk()
        } else {
          // TODO : add alert dialog to the view
          Log.e("SignUpViewModel", "Error: ${e.message}")
        }
      }
    }
  }
}
