package com.github.se.gatherspot.ui.profile

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.model.Interests


public class InterestsView {
    @Preview
    @Composable
    fun InterestsPreview() {
        EditInterests(OwnProfileViewModel())
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun EditInterests(viewModel: OwnProfileViewModel) {
        val interestList = enumValues<Interests>().toList()
        val set = viewModel.interests
        val swap = viewModel::swapInterest
        FlowRow()
        {
            interestList.forEach { interest ->
                val selected = set.contains(interest)
                EditableInterest(interest, selected) { swap(interest, selected) }
            }
        }
    }
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun ShowInterests(viewModel: OwnProfileViewModel) {
        val interestList = enumValues<Interests>().toList()
        val set = viewModel.interests
        FlowRow()
        {
            interestList.forEach { interest ->
                UneditableInterest(interest, set.contains(interest))
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun EditableInterest(interest: Interests, selected: Boolean, onClick: (Int) -> Unit) {

        FilterChip(
            onClick = { onClick(interest.ordinal) },
            label = {
                Text(interest.name)
            },
            selected = selected,
            leadingIcon = {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun UneditableInterest(interest: Interests,selected: Boolean){
        if (selected){
        FilterChip(
            onClick = {},
            label = {
                Text(interest.name)
            },
            selected = true,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        }
    }
}