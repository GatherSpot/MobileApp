package com.github.se.gatherspot.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.setUp.SetUpView
import com.github.se.gatherspot.ui.setUp.SetUpViewModel

@Preview
@Composable
fun SetUpProfilePreview() {
  val nav = rememberNavController()
  SetUpProfile(NavigationActions(nav))
}

@Composable
fun SetUpProfile(nav: NavigationActions) {
  val vm = SetUpViewModel(nav)
  SetUpView(vm)
//  val auth = FirebaseAuth.getInstance()
//  var isEmailVerified by remember { mutableStateOf(false) }
//  var emailText by remember { mutableStateOf("") }
//  var isClicked by remember { mutableStateOf(false) }
//  val allCategories = enumValues<Interests>().toList()
//  val interests by remember { mutableStateOf(mutableSetOf<Interests>()) }
//
//  LaunchedEffect(isClicked) {
//    if (isClicked) {
//      ProfileFirebaseConnection().updateInterests(uid, interests)
//      nav.controller.navigate("profile")
//    }
//  }
//
//  Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp).testTag("setUpScreen")) {
//    Text(text = "Choose your interests", fontSize = 30.sp)
//    Spacer(modifier = Modifier.height(30.dp))
//    LazyColumn(Modifier.weight(1f).testTag("lazyColumn")) {
//      items(allCategories) { interest ->
//        FilterChipCompose(interest, interests, Modifier.testTag(interest.toString()))
//        Spacer(modifier = Modifier.height(2.dp))
//      }
//    }
//
//    Column {
//      Spacer(modifier = Modifier.height(20.dp))
//      Text("You can change your interests at any time in your profile settings")
//      Spacer(modifier = Modifier.height(20.dp))
//      Button(
//          colors = ButtonDefaults.buttonColors(Color.Transparent),
//          onClick = { isClicked = !isClicked },
//          modifier =
//              Modifier.testTag("saveButton")
//                  .clickable { isClicked = !isClicked }
//                  .border(width = 0.7.dp, Color.Black, shape = RoundedCornerShape(100.dp))
//                  .wrapContentSize()) {
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = androidx.compose.ui.Alignment.Center) {
//                  Text("Save", color = Color.Black, fontSize = 22.sp)
//                }
//          }
//      Spacer(modifier = Modifier.height(3.dp))
//      Box(
//          modifier = Modifier.fillMaxWidth(),
//          contentAlignment = androidx.compose.ui.Alignment.Center) {
//            Text(text = emailText, color = Color.Red, modifier = Modifier.testTag("emailText"))
//          }
//    }
//  }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FilterChipCompose(interest: Interests, interests: MutableSet<Interests>, modifier: Modifier) {
//  var selected by remember { mutableStateOf(false) }
//
//  FilterChip(
//      onClick = {
//        selected = !selected
//        if (selected) {
//          interests.add(interest)
//        } else {
//          interests.remove(interest)
//        }
//      },
//      label = {
//        Text(
//            (interest.toString().lowercase().replaceFirstChar { c -> c.uppercase() }),
//            fontSize = 20.sp,
//            color = Color.Black)
//      },
//      selected = selected,
//      leadingIcon = {
//        if (selected) {
//          Icon(
//              imageVector = Icons.Filled.Done,
//              contentDescription = "Done icon",
//              modifier = Modifier.size(FilterChipDefaults.IconSize))
//        } else {
//          Icon(
//              imageVector = Icons.Filled.Add,
//              contentDescription = "Add icon",
//              modifier = Modifier.size(FilterChipDefaults.IconSize + 2.dp))
//        }
//      },
//      modifier = modifier)
}
