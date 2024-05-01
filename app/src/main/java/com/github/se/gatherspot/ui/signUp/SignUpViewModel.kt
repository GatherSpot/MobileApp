package com.github.se.gatherspot.ui.signUp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth

class SignUpViewModel(val nav: NavigationActions) {
  var userName = MutableLiveData("")
  var email = MutableLiveData("")
  var password = MutableLiveData("")
  var doesUserNameExist = MutableLiveData<Boolean>()
  var isEmailValid = MutableLiveData<Boolean>()
  var emailError = MutableLiveData("")
  var isPasswordValid = MutableLiveData<Boolean>()
  var isPassWordVisible = MutableLiveData(false)
  var isEverythingOk = MutableLiveData<Boolean>()
  private fun updateEverythingOk() {
    isEverythingOk.value = (doesUserNameExist.value == false && isEmailValid.value == true && isPasswordValid.value == true)
    println("isEverythingOk: ${isEverythingOk.value} isPasswordValid: ${isPasswordValid.value} isEmailValid: ${isEmailValid.value} doesUsernameExist: ${doesUserNameExist.value}")
    println("isEverythingOk: ${isEverythingOk.value}")
  }
  fun navBack() {
    nav.controller.navigate("auth")
  }
  fun updateUsername(string: String) {
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
    val passwordRegex = """^((?=\S*?[A-Z])(?=\S*?[a-z])(?=\S*?[0-9]).{6,})\S$"""
    isPasswordValid.value = password.value!!.matches(passwordRegex.toRegex())
    updateEverythingOk()
  }

  fun flipPasswordVisibility() {
    isPassWordVisible.value = !(isPassWordVisible.value!!)
  }

  fun signUp() {
    Firebase.auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
      .addOnSuccessListener(){
        Profile.add(userName.value!!,Firebase.auth.uid!!)
        Firebase.auth.currentUser!!.sendEmailVerification()
        nav.controller.navigate("setup")
      }
      .addOnFailureListener(){
        when (it) {
          is FirebaseAuthUserCollisionException -> {
            emailError.value = "Email already in use"
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
