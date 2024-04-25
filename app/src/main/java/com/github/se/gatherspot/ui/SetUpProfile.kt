package com.github.se.gatherspot.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Preview
@Composable
fun SetUpProfilePreview() {
  val nav = rememberNavController()
  SetUpProfile(NavigationActions(nav), "uid")
}

@Composable
fun SetUpProfile(nav: NavigationActions, uid: String) {

  val auth = FirebaseAuth.getInstance()
  var isEmailVerified by remember { mutableStateOf(false) }
  var emailText by remember { mutableStateOf("") }
  var isClicked by remember { mutableStateOf(false) }
  val allCategories = enumValues<Interests>().toList()
  val interests by remember { mutableStateOf(mutableSetOf<Interests>()) }

  LaunchedEffect(isClicked) {
    withContext(Dispatchers.Main) {
      auth.currentUser?.reload()?.await()
      isEmailVerified = auth.currentUser?.isEmailVerified ?: false
      if (isEmailVerified) {
        ProfileFirebaseConnection().updateInterests(uid, interests)
        nav.controller.navigate("profile")
      } else {
        emailText = "Please verify your email before continuing"
      }
    }
  }

  Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpScreen")) {
    Text(text = "Choose your interests", fontSize = 30.sp)
    Spacer(modifier = Modifier.height(30.dp))
    LazyColumn(Modifier.weight(1f).testTag("lazyColumn")) {
      items(allCategories) { interest ->
        FilterChipCompose(interest, interests, Modifier.testTag(interest.toString()))
        Spacer(modifier = Modifier.height(2.dp))
      }
    }

    Column {
      Spacer(modifier = Modifier.height(20.dp))
      Text("You can change your interests at any time in your profile settings")
      Spacer(modifier = Modifier.height(20.dp))
      Button(
          colors = ButtonDefaults.buttonColors(Color.Transparent),
          onClick = { isClicked = !isClicked },
          modifier =
              Modifier.testTag("saveButton")
                  .clickable { isClicked = !isClicked }
                  .border(width = 0.7.dp, Color.Black, shape = RoundedCornerShape(100.dp))
                  .wrapContentSize()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center) {
                  Text("Save", color = Color.Black, fontSize = 22.sp)
                }
          }
      Spacer(modifier = Modifier.height(3.dp))
      Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = emailText, color = Color.Red, modifier = Modifier.testTag("emailText"))
          }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipCompose(interest: Interests, interests: MutableSet<Interests>, modifier: Modifier) {
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
      label = {
        Text(
            (interest.toString().lowercase().replaceFirstChar { c -> c.uppercase() }),
            fontSize = 20.sp,
            color = Color.Black)
      },
      selected = selected,
      leadingIcon = {
        if (selected) {
          Icon(
              imageVector = Icons.Filled.Done,
              contentDescription = "Done icon",
              modifier = Modifier.size(FilterChipDefaults.IconSize))
        } else {
          Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = "Add icon",
              modifier = Modifier.size(FilterChipDefaults.IconSize + 2.dp))
        }
      },
      modifier = modifier)
}
