package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo

class Dependencies(val authentication: Authentication,
                   val playersRepo: PlayerRepo,
                   val matchRepo : MatchRepo)
