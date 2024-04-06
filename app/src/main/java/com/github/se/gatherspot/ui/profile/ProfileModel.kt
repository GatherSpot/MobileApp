package com.github.se.gatherspot.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gatherspot.R

@Composable
fun BioField(edit: Boolean, bio: String, updateBio: (String) -> Unit) {
    OutlinedTextField(
        label = { Text("Bio") },
        enabled = edit,
        value = bio,
        onValueChange = { updateBio(it) },
        modifier =
        Modifier.height(150.dp).fillMaxWidth().padding(8.dp).semantics {
            contentDescription = "bio"
        })
}

@Composable
fun UsernameField(edit: Boolean, username: String, updateUsername: (String) -> Unit) {
    OutlinedTextField(
        modifier =
        Modifier.fillMaxWidth().padding(8.dp).semantics { contentDescription = "username" },
        label = { Text("username") },
        enabled = edit,
        value = username,
        onValueChange = { updateUsername(it) })
}
//this is a bit clunky, it changes the button type if edit is changed, might need rework into two screens to clean this behavior
@Composable
fun Buttons(edit: Boolean, toggleEdit: () -> Unit, cancel: () -> Unit, save: () -> Unit) {
    if (edit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Cancel",
                modifier =
                Modifier.clickable {
                    cancel()
                    toggleEdit()
                }
                    .semantics { contentDescription = "cancel" })
            Text(
                text = "Save",
                modifier =
                Modifier.clickable {
                    save()
                    toggleEdit()
                }
                    .semantics { contentDescription = "save" })
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
            // Text(text = "Edit", modifier = Modifier.clickable { edit = true })
            Icon(
                painter = painterResource(R.drawable.edit),
                contentDescription = "edit",
                modifier =
                Modifier.clickable { toggleEdit() }
                    .size(24.dp)
                    .semantics { contentDescription = "edit" })
        }
    }
}

@Composable
fun ProfileImage(edit: Boolean, imageUri: String) {
    val painter = rememberAsyncImagePainter(imageUri.ifEmpty { R.drawable.user })
    Column(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
            Image(
                painter = painter,
                contentDescription = "Profile Image",
                modifier = Modifier.clickable { /*select image*/},
                contentScale = ContentScale.Crop)
        }
        if (edit) Text(text = "Change profile picture")
    }
}
/**
 * This is used to show someone's own profile, and it is editable
 * @param viewModel the view model that holds the profile data
 */
@Composable
fun OwnProfile(viewModel: OwnProfileViewModel) {
    //syntactic sugar for the view model values with sane defaults, that way the rest of code looks nice
    val edit by viewModel.edit.observeAsState(initial = false)
    val username by viewModel.username.observeAsState(initial = "")
    val bio by viewModel.bio.observeAsState(initial = "")
    val imageUri by viewModel.image.observeAsState(initial = "")
    val cancelProfile = { viewModel.cancel() }
    val saveProfile = { viewModel.save() }
    val updateUsername = { s: String -> viewModel.updateUsername(s) }
    val updateBio = { s: String -> viewModel.updateBio(s) }
    val toggleEdit = { viewModel.toggleEdit() }
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
        Buttons(edit, toggleEdit, cancelProfile, saveProfile)
        ProfileImage(edit, imageUri)
        UsernameField(edit, username, updateUsername)
        BioField(edit, bio,updateBio)
    }
}
/**
 * This is used to show someone else's profile, and it is not editable
 * @param viewModel a view model that holds the profile to show
 */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val edit = false
    val username = viewModel.username
    val bio = viewModel.bio
    val imageUri = viewModel.image

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
        ProfileImage(edit, imageUri)
        UsernameField(edit, username) {}
        BioField(edit, bio) {}
    }
}