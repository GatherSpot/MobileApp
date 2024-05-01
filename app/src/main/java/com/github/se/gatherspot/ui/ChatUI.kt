package com.github.se.gatherspot.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.chat.ChatMessage
import com.github.se.gatherspot.model.chat.ChatViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
fun ChatUI(viewModel: ChatViewModel, currentUserId: String, navActions: NavigationActions) {
  val messages = viewModel.messages.collectAsState().value
  val listState = rememberLazyListState()
  val coroutineScope =
      rememberCoroutineScope() // Remember a CoroutineScope tied to the Composable's lifecycle
  var text by remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier.testTag("ChatUIScreen"),
      topBar = {
        val title = if (viewModel.event == null) "Event Chat" else viewModel.event!!.title
        CustomTopAppBar(navActions = navActions, title = title)
      },
      bottomBar = {
        MessageInputField(
            text = text,
            onTextChange = { text = it },
            onSend = {
              if (text.isNotBlank()) {
                viewModel.addMessage(UUID.randomUUID().toString(), currentUserId, text)
                text = ""
                coroutineScope.launch {
                  // Scroll to the bottom when a new message is sent
                  listState.animateScrollToItem(messages.size)
                }
              }
            })
      }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().testTag("messagesList"),
            state = listState) {
              coroutineScope.launch {
                // Scroll to the bottom when a new message is sent
                listState.animateScrollToItem(messages.size)
              }
              items(messages) { message ->
                ChatMessageCard(
                    message = message, isCurrentUser = message.senderId == currentUserId)
              }
              // Add a small space at the end of the list
              item { Spacer(modifier = Modifier.height(8.dp).testTag("spacer")) }
            }
      }
}

@Composable
fun ChatMessageCard(message: ChatMessage, isCurrentUser: Boolean) {
  val backgroundColor = if (isCurrentUser) Color(0xFF90EE90) else Color.White
  val padding =
      if (isCurrentUser) PaddingValues(end = 16.dp, top = 4.dp, bottom = 4.dp)
      else PaddingValues(start = 16.dp, top = 4.dp, bottom = 4.dp)

  Box(
      modifier = Modifier.fillMaxWidth().padding(padding).testTag("boxChatMessageCard"),
      contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart) {
        Card(
            backgroundColor = backgroundColor,
            elevation = 4.dp,
            modifier = Modifier.padding(4.dp).testTag("chatMessageCard")) {
              Text(
                  text = message.message,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(8.dp))
            }
      }
}

@Composable
fun CustomTopAppBar(navActions: NavigationActions, title: String) {
  TopAppBar(
      modifier = Modifier.testTag("chatTopBar"),
      title = { Text(text = title, color = Color.Black) },
      navigationIcon = {
        IconButton(onClick = { navActions.goBack() }) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Go Back",
              tint = Color.Black)
        }
      },
      backgroundColor = Color.White,
      contentColor = Color.Black,
      elevation = 4.dp)
}

@Composable
fun MessageInputField(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
  Row(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f).padding(end = 8.dp).testTag("inputMessage"),
            placeholder = { Text("Type a message...") },
            shape = RoundedCornerShape(20.dp),
            colors =
                TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFFAFAFA),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent))
        FloatingActionButton(
            modifier = Modifier.testTag("sendButton"),
            onClick = onSend,
            backgroundColor = MaterialTheme.colors.primary) {
              Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
      }
}

@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
  val eventid = "886856f1-f709-41cf-b3c3-fb872c59eb91"
  val eventFirebaseConnection = com.github.se.gatherspot.EventFirebaseConnection()
  val event =
      Event(
          id = eventid,
          title = "Test Event",
          description = "This is a test event",
          location = Location(0.0, 0.0, "Test Location"),
          eventStartDate =
              LocalDate.parse(
                  "12/04/2026", DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT)),
          eventEndDate =
              LocalDate.parse(
                  "12/05/2026", DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT)),
          timeBeginning =
              LocalTime.parse(
                  "10:00", DateTimeFormatter.ofPattern(eventFirebaseConnection.TIME_FORMAT)),
          timeEnding =
              LocalTime.parse(
                  "12:00", DateTimeFormatter.ofPattern(eventFirebaseConnection.TIME_FORMAT)),
          attendanceMaxCapacity = 100,
          attendanceMinCapacity = 10,
          inscriptionLimitDate =
              LocalDate.parse(
                  "10/04/2025", DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT)),
          inscriptionLimitTime =
              LocalTime.parse(
                  "09:00", DateTimeFormatter.ofPattern(eventFirebaseConnection.TIME_FORMAT)),
          eventStatus = EventStatus.DRAFT,
          categories = setOf(Interests.CHESS),
          registeredUsers = mutableListOf("my_id"),
          finalAttendees = emptyList(),
          images = null,
          globalRating = null)
  eventFirebaseConnection.add(event)
  ChatUI(ChatViewModel(eventid), "my_id", NavigationActions(rememberNavController()))
}
