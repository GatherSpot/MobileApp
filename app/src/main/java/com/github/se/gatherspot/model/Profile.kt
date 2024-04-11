package com.github.se.gatherspot.model

import com.github.se.gatherspot.model.event.Event

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile(
    val uid: String,
    private var _userName: String,
    private var _bio: String,
    private var _image: String,
    private var _interests : Set<Interests>?
) {

    var userName: String = _userName
    var bio: String = _bio
    var image: String = _image
    var interests : Set<Interests> = _interests ?: hashSetOf()
    var upcomingEvents : List<Event> = listOf()
    var history : List<Event> = listOf()


    constructor(uid: String, userName: String) : this(uid, userName, "","", hashSetOf() )
    constructor() : this("", "", "", "", hashSetOf())
    companion object {
        fun defaultProfile(user: User) : Profile{
            return Profile(user.uid, user.email, "", "", hashSetOf())
        }
    }
}
