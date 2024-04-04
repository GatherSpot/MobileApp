package com.github.se.gatherspot.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ChipDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.model.Category
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SetUpProfile(nav: NavigationActions, uid: String) {

  val auth = FirebaseAuth.getInstance()
  var isEmailVerified by remember { mutableStateOf(false) }
  var emailText by remember { mutableStateOf("") }
  var isClicked by remember { mutableStateOf(false) }
  val allCategories = enumValues<Category>().toList()
  var interests by remember { mutableStateOf(mutableSetOf<Category>()) }

  LaunchedEffect(isClicked, interests) {
    if (isClicked) {
      withContext(Dispatchers.Main) {
        auth.currentUser?.reload()?.await()
        isEmailVerified = auth.currentUser!!.isEmailVerified
        if (isEmailVerified) {
          UserFirebaseConnection.updateUserInterests(uid, Profile(interests))
          nav.controller.navigate("profile")
        } else {
          emailText = "Please verify your email before continuing"
        }
      }
    }
  }

  Column(
      modifier = Modifier
          .padding(horizontal = 20.dp, vertical = 30.dp)
          .testTag("setUpProfile")) {
        Text(text = "Choose your interests", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn {
          items(allCategories) { interest ->
            FilterChipCompose(interest, interests)
            Spacer(modifier = Modifier.height(2.dp))
          }
        }

        Text("You can change your interests at any time in your profile")
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            onClick = { isClicked = true },
            modifier =
            Modifier
                .testTag("saveButton")
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(100.dp))
                .padding(horizontal = 100.dp)
                .wrapContentSize()) {
              Text("Save", color = Color.Black, fontSize = 19.sp)
            }
        Text(text = emailText, color = Color.Red)
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipCompose(interest: Category, interests: MutableSet<Category>) {
  var selected by remember { mutableStateOf(false) }

  FilterChip(
      onClick = {
        selected = !selected
        if (selected) {
          interests.add(interest)
        } else {
          interests.remove(interest)
        }
      },
      label = { Text((interest.toString().lowercase().capitalize()), fontSize = 20.sp, color = Color.Black) },
      selected = selected,
      leadingIcon =
      {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize))
                }
             else {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize + 2.dp))
            }
      },
  )
}