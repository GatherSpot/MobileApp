@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.se.gatherspot.model.FormListener

@OptIn(ExperimentalMaterial3Api::class)

class TopBarSaveCancelButton(
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
    val title: @Composable () -> Unit,
    val isUpToDate : MutableState<Boolean>
) : FormListener {

    @Composable
    fun Display(){
        if (isUpToDate.value) {
            FormIsUptoDate()
        } else {
            FormIsEdited()
        }
    }



    @Composable
    fun FormIsEdited() {
        // Top app bar with save and cancel buttons


        CenterAlignedTopAppBar(
            title = title
            ,
            navigationIcon = {
                IconButton(onClick = {
                    onCancel()
                    isUpToDate.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Localized description",
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSave()
                    isUpToDate.value = true}) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Localized description"
                    )
                }
            }
        )
    }

    @Composable
    fun FormIsUptoDate(
    ) {
        CenterAlignedTopAppBar(
            title = title

        )

    }

}
