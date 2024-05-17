package com.github.se.gatherspot.ui

<<<<<<< HEAD
=======
import android.os.Build
import androidx.annotation.RequiresApi
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
>>>>>>> main
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
<<<<<<< HEAD
=======
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.MapViewModel
>>>>>>> main
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.setUp.SetUpView
import com.github.se.gatherspot.ui.setUp.SetUpViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun SetUpProfilePreview() {
  val nav = rememberNavController()
  SetUpProfile(NavigationActions(nav))
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
<<<<<<< HEAD
fun SetUpProfile(nav: NavigationActions) {
  val storeOwner = LocalViewModelStoreOwner.current!!
  val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner = storeOwner)
  SetUpView(viewModel, nav)
=======
fun SetUpProfile(nav: NavigationActions, uid: String) {

  val auth = FirebaseAuth.getInstance()
  var isEmailVerified by remember { mutableStateOf(false) }
  var emailText by remember { mutableStateOf("") }
  var isClicked by remember { mutableStateOf(false) }
  val allCategories = enumValues<Interests>().toList()
  val interests by remember { mutableStateOf(mutableSetOf<Interests>()) }

  LaunchedEffect(isClicked) {
    if (isClicked) {
      withContext(Dispatchers.Main) {
        auth.currentUser?.reload()?.await()
        isEmailVerified = auth.currentUser?.isEmailVerified ?: false
        if (isEmailVerified) {
          ProfileFirebaseConnection().updateInterests(uid, interests)
          if (MainActivity.mapViewModel == null) {
            MainActivity.mapViewModel = MapViewModel(MainActivity.app)
          }
          nav.controller.navigate("profile")
        } else {
          emailText = "Please verify your email before continuing"
        }
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
  var icon by remember {
    mutableStateOf(
        if (selected || interests.contains(interest)) {
          Icons.Filled.Done
        } else {
          Icons.Filled.Add
        })
  }

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
      selected = selected || interests.contains(interest),
      leadingIcon = {
        if (selected || interests.contains(interest)) {
          Icon(
              imageVector = Icons.Filled.Done,
              contentDescription = "Done icon",
              modifier = Modifier.size(FilterChipDefaults.IconSize).testTag("remove${interest}"))
        } else {
          Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = "Add icon",
              modifier =
                  Modifier.size(FilterChipDefaults.IconSize + 2.dp).testTag("add${interest}"))
        }
      },
      modifier = modifier)
>>>>>>> main
}
