package com.gregmcgowan.fivesorganiser.data.player

data class Player(
    val playerId: String,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val contactId: Long? = null,
)
