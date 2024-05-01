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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.profile.InterestsView
import com.github.se.gatherspot.ui.profile.ProfileView

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
fun DoneButton(next: () -> Unit) {
  Button(onClick = { next() }, modifier = Modifier.testTag("doneButton").wrapContentSize()) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = androidx.compose.ui.Alignment.Center) {
          Text("Start using the app", fontSize = 22.sp)
        }
  }
}

@Composable
fun SetUpView(vm: SetUpViewModel) {
  val interests = vm.interests.observeAsState()
  val currentState = vm.currentState.observeAsState()
  val bio = vm.bio.observeAsState()
  val flipInterests = vm::flipInterests
  val next = vm::next
  val setBio = vm::setBio
  val done = vm::done
  if (currentState.value == SetUpViewModel.Phases.INTERESTS) {
    Column(
        modifier =
            Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpInterests")) {
          // TODO : add scroll ???
          // TODO : add condition minimum 3 interests ?
          Text(text = "Choose your interests", fontSize = 30.sp)
          Spacer(modifier = Modifier.height(30.dp))
          InterestsView().EditInterests(Interests.toList(), interests, flipInterests)
          NextButton(next)
        }
  }
  if (currentState.value == SetUpViewModel.Phases.BIO) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpBio")) {
      Text(text = "Tell us about yourself", fontSize = 30.sp)
      Spacer(modifier = Modifier.height(30.dp))
      ProfileView().BioField(bio = bio.value!!, updateBio = { setBio(it) }, edit = true)
      NextButton(next)
    }
  }
  if (currentState.value == SetUpViewModel.Phases.IMAGE) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpImage")) {
          Text(text = "Choose a profile picture", fontSize = 30.sp)
          Spacer(modifier = Modifier.height(30.dp))
          // TODO : add image picker
          Text(text = "Not implemented yet, maybe in v3 :)")
          NextButton(next)
        }
  }
  if (currentState.value == SetUpViewModel.Phases.DONE) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpDone")) {
      Text(text = "You're all set!", fontSize = 30.sp)
      Spacer(modifier = Modifier.height(30.dp))
      Text(text = "Welcome and have fun using GatherSpot !!!", fontSize = 20.sp)
      DoneButton(done)
    }
  }
}
