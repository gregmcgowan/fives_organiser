package com.gregmcgowan.fivesorganiser.players

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(val name : String = "Unknown Player",
                  val phoneNumber : String = "",
                  val email : String = "",
                  val contactId : Int = -1)
