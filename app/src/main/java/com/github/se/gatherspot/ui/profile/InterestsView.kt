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
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.model.Interests

public class InterestsView {
  @Preview
  @Composable
  fun InterestsPreview() {
    val interests = MutableLiveData(Interests.addInterest(Interests.new(), Interests.FOOTBALL))
    EditInterests(
        interestList = Interests.toList(),
        interests = interests.observeAsState(),
    ) {
      interests.value = Interests.flipInterest(interests.value ?: setOf(), it)
    }
  }

  @OptIn(ExperimentalLayoutApi::class)
  @Composable
  fun EditInterests(
      interestList: List<Interests>,
      interests: State<Set<Interests>?>,
      swap: (Interests) -> Unit
  ) {
    FlowRow(modifier = Modifier.testTag("interestsEdit")) {
      interestList.forEach { interest ->
        val selected = interests.value?.contains(interest) ?: false
        EditableInterest(interest, selected) { swap(interest) }
      }
    }
  }

  @OptIn(ExperimentalLayoutApi::class)
  @Composable
  fun ShowInterests(set: Set<Interests>) {
    FlowRow(modifier = Modifier.testTag("interestsShow")) {
      set.forEach { interest -> UneditableInterest(interest, set.contains(interest)) }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun EditableInterest(interest: Interests, selected: Boolean, onClick: (Int) -> Unit) {

    FilterChip(
        onClick = { onClick(interest.ordinal) },
        label = { Text(interest.name) },
        selected = selected,
        leadingIcon = {
          if (selected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "remove ${interest.name}",
                modifier = Modifier.size(FilterChipDefaults.IconSize))
          } else {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add ${interest.name}",
                modifier = Modifier.size(FilterChipDefaults.IconSize))
          }
        },
        modifier =
            Modifier.padding(horizontal = 4.dp)
                .testTag(if (selected) "remove ${interest.name}" else "add ${interest.name}"))
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun UneditableInterest(interest: Interests, selected: Boolean) {
    if (selected) {
      FilterChip(
          onClick = {},
          label = { Text(interest.name) },
          selected = true,
          modifier = Modifier.padding(horizontal = 4.dp).testTag(interest.name))
    }
  }
}
