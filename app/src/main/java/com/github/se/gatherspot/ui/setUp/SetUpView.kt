package com.github.se.gatherspot.ui.setUp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.R
import com.github.se.gatherspot.intents.ImagePicker
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.BioField
import com.github.se.gatherspot.ui.profile.InterestsView

@Composable
fun NextButton(next: () -> Unit) {
  Button(
      colors = ButtonDefaults.buttonColors(Color.Transparent),
      onClick = { next() },
      modifier =
          Modifier.testTag("nextButton")
              .border(width = 0.7.dp, Color.Black, shape = RoundedCornerShape(100.dp))
              .wrapContentSize()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center) {
              Text("Next", color = Color.Black, fontSize = 22.sp)
            }
      }
}

@Composable
fun DoneButton(isDone: Boolean, nav: NavigationActions) {
  Button(
      enabled = isDone,
      onClick = { nav.controller.navigate("home") },
      modifier = Modifier.testTag("doneButton").wrapContentSize()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center) {
              Text("Start using the app", fontSize = 22.sp)
            }
      }
}

@Composable
private fun Interests(vm: SetUpViewModel) {
  val interests = vm.interests.observeAsState()
  val flipInterests = vm::flipInterests
  Column(
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpInterests")) {
        // TODO : add scroll ???
        // TODO : add condition minimum 3 interests ?
        Text(text = "Choose your interests", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(30.dp))
        InterestsView().EditInterests(Interests.toList(), interests, flipInterests)
      }
}

@Composable
private fun Bio(vm: SetUpViewModel) {
  val bio = vm.bio.observeAsState("")
  val bioError = vm.bioError.observeAsState("")
  val setBio = vm::setBio
  Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpBioTag")) {
    Text(text = "Tell us a bit about yourself", fontSize = 30.sp)
    Spacer(modifier = Modifier.height(30.dp))
    BioField(bio, bioError, setBio, edit = true)
  }
}

@Composable
private fun Image(vm: SetUpViewModel) {
  Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpImage")) {
    Text(text = "Choose a profile picture", fontSize = 30.sp)
    Spacer(modifier = Modifier.height(30.dp))
    ImagePicker(
        imageUri = vm.image.observeAsState(""),
        placeHolder = R.drawable.user,
        pictureName = "profile",
        updateImageUri = vm::setImage,
        deleteImage = vm::deleteImage)
  }
}

@Composable
private fun Done() {
  Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpDone")) {
    Text(text = "You're all set!", fontSize = 30.sp)
    Spacer(modifier = Modifier.height(30.dp))
    Text(text = "Welcome and have fun using GatherSpot !!!", fontSize = 20.sp)
  }
}

@Composable
fun SetUpView(vm: SetUpViewModel, nav: NavigationActions) {
  val currentStep = vm.currentStep.observeAsState()
  val isDone = vm.isDone.observeAsState(false)
  val doneButton = vm.doneButton.observeAsState(false)
  Scaffold(
      bottomBar = {
        Box(modifier = Modifier.padding(16.dp)) {
          if (doneButton.value == true) DoneButton(isDone.value, nav) else NextButton(vm::next)
        }
      },
      content = { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
          when (currentStep.value) {
            "Interests" -> Interests(vm)
            "Bio" -> Bio(vm)
            "Image" -> Image(vm)
            "Done" -> Done()
          }
        }
      })
}
