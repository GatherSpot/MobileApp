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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gatherspot.R
import com.github.se.gatherspot.data.Profile

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
                modifier = Modifier.clickable { /*TODO: select image*/},
                contentScale = ContentScale.Crop)
        }
        if (edit) Text(text = "Change profile picture")
    }
}

@Composable
fun EditableProfileScreen(getProfile: ()-> Profile, saveProfile: (String, String, String) -> Unit) {
    var edit: Boolean by remember { mutableStateOf(false) }
    val profile = getProfile()
    // Note: we need duplicate states because we need to keep the original values when the user
    // cancels
    var username: String by remember { mutableStateOf(profile.getUserName()) }
    var bio: String by remember { mutableStateOf(profile.getBio()) }
    var imageUri: String by remember { mutableStateOf(profile.getImage()) }
    // helper functions to update states

    fun cancelProfile() {
        username = profile.getUserName()
        bio = profile.getBio()
        imageUri = profile.getImage()
    }
    val toggleEdit = { edit = !edit }
    val updateUsername = { it: String -> username = sanitizeUsername(it) }
    val updateBio = { it: String -> bio = sanitizeBio(it) }
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
        Buttons(edit, toggleEdit, { cancelProfile() }, { saveProfile(username, bio, imageUri) })
        ProfileImage(edit, imageUri)
        UsernameField(edit, username, updateUsername)
        BioField(edit, bio, updateBio)
    }
}
@Composable
fun ProfileScreen(getProfile: ()->Profile) {
    val profile = getProfile()
    val edit = false
    val username = profile.getUserName()
    val bio = profile.getBio()
    val imageUri = profile.getImage()

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
        ProfileImage(edit, imageUri)
        UsernameField(edit, username) {}
        BioField(edit, bio) {}
    }
}
@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen {
        Profile(
            "John Doe",
            "Leafy greens enjoyer, haskell enthousiast",
            ""
        )
    }
}
@Preview
@Composable
fun EditableProfileScreenPreview() {
    var profile = Profile(
        "John Doe",
        "Leafy greens enjoyer, haskell enthousiast",
        ""
    )

    val changeProfile: (String, String, String) -> Unit = { newUsername, newBio, newImageUri ->
        profile = Profile(newUsername, newBio, newImageUri)
    }

    EditableProfileScreen(
        { profile },
        changeProfile
    )
}