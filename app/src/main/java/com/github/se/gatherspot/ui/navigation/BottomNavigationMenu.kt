package com.github.se.gatherspot.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String?
) {

  BottomNavigation(backgroundColor = Color.LightGray) {
    tabList.forEach { tld ->
      BottomNavigationItem(
          icon = {
            Icon(
                painter = painterResource(tld.icon),
                contentDescription = null,
                modifier = Modifier.width(30.dp).height(30.dp))
          },
          label = { Text(stringResource(tld.textId), fontSize = 12.sp) },
          selected = selectedItem == tld.route,
          onClick = { onTabSelect(tld) })
    }
  }
}
