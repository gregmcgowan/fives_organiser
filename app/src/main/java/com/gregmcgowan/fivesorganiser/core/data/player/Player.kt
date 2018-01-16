package com.gregmcgowan.fivesorganiser.core.data.player


data class Player(val playerId : String = "",
                  val name: String = "Unknown Player",
                  val phoneNumber: String = "",
                  val email: String = "",
                  val contactId: Long = -1)
