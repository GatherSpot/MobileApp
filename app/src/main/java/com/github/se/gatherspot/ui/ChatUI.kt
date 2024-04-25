package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.model.chat.ChatMessage
import com.github.se.gatherspot.model.chat.ChatViewModel
import java.util.UUID

@Composable
fun ChatUI(viewModel: ChatViewModel) {
  val messages by viewModel.messages.collectAsState()
  var currentMessageText by remember { mutableStateOf("") }
  val keyboardController = LocalSoftwareKeyboardController.current

  Scaffold(
      topBar = { TopAppBar(title = { Text("Chat Room") }, backgroundColor = Color.LightGray) },
      bottomBar = {
        BottomAppBar(backgroundColor = Color.White) {
          TextField(
              value = currentMessageText,
              onValueChange = { currentMessageText = it },
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              placeholder = { Text("Type a message...") },
              keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
              keyboardActions =
                  KeyboardActions(
                      onSend = {
                        viewModel.addMessage(
                            UUID.randomUUID().toString(), "senderIdPlaceholder", currentMessageText)
                        currentMessageText = "" // Reset the text field
                        keyboardController?.hide() // Dismiss the keyboard
                      }),
              trailingIcon = {
                IconButton(
                    onClick = {
                      viewModel.addMessage(
                          UUID.randomUUID().toString(), "senderIdPlaceholder", currentMessageText)
                      currentMessageText = "" // Reset the text field
                      keyboardController?.hide() // Dismiss the keyboard
                    }) {
                      Icon(Icons.Filled.Send, contentDescription = "Send Message")
                    }
              },
              singleLine = true)
        }
      }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
          LazyColumn(
              modifier = Modifier.fillMaxSize(),
              reverseLayout = true, // Start the chat from the bottom
              verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(messages.reversed()) { message -> // Reverse for correct display order
                  MessageCard(message)
                }
              }
        }
      }
}

@Composable
fun MessageCard(message: ChatMessage) {
  Card(
      backgroundColor = Color(0xFFEFEFEF),
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(
              text = "From: ${message.senderId}",
              style = MaterialTheme.typography.body2,
              color = Color.DarkGray)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = message.message, style = MaterialTheme.typography.body1)
        }
      }
}
