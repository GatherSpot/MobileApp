package com.github.se.gatherspot.model

import com.github.se.gatherspot.model.event.Event

data class Chat(val chatID: String, val people: List<Profile>, val event: Event)
