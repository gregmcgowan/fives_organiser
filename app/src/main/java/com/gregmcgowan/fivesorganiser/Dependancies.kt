package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.authenication.Authentication
import com.gregmcgowan.fivesorganiser.players.PlayerRepo

class Dependancies(val authentication : Authentication,
                   val playersRepo: PlayerRepo)
