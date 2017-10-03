package com.gregmcgowan.fivesorganiser.core.data.player

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PlayerEntity(val playerId : String = "",
                        val name: String = "Unknown Player",
                        val phoneNumber: String = "",
                        val email: String = "",
                        val contactId: Int = -1)
