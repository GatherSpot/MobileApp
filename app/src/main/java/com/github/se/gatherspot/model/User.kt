package com.github.se.gatherspot.model

import com.github.se.gatherspot.CollectionClass

data class User(
    override val id: String,
    val username: String,
    val email: String,
    val password: String
) : CollectionClass()
