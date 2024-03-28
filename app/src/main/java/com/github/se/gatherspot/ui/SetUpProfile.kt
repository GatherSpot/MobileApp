package com.github.se.gatherspot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.FirebaseConnection
import com.github.se.gatherspot.model.Category
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions


@Composable
fun SetUpProfile(nav: NavigationActions, uid: String) {

    val allCategories = enumValues<Category>().toList()
    val interests = mutableSetOf<Category>()
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)) {
        LazyColumn {
            items(allCategories) { interest ->
                ToggleButton(interest, interests)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("You can change your interests at any time in your profile")
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            onClick = {
                FirebaseConnection.updateUserInterests(uid, Profile(interests))
                nav.controller.navigate("profile")
                      },
            modifier = Modifier.border(
                width = 1.dp, color = Color.Black,
                shape = RoundedCornerShape(100.dp)
            ).padding(horizontal = 120.dp)
            ) {
                Text("Save", color = Color.Black)
            }
        }
}

@Composable
fun ToggleButton(interest: Category, interests: MutableSet<Category>){
    var selected by remember { mutableStateOf(false) }
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        onClick = {
            selected = !selected
            if(selected){
                interests.add(interest)
            }
            else{
                interests.remove(interest)
            }
        },
        modifier = Modifier.border(width = 1.dp,
            color = if(selected) Color.Black else Color.LightGray,
            shape = RoundedCornerShape(200.dp)
           )
    ){
        Text(interest.toString(), fontSize = 20.sp, color = Color.Black)
    }
}



