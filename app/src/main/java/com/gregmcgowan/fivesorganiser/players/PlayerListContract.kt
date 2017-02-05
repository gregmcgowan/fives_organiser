package com.gregmcgowan.fivesorganiser.players

interface PlayerListContract {

    interface View {
        fun showPlayers(players: List<Player>)
        fun showProgressBar(show : Boolean)
        fun showPlayerList(show : Boolean)
    }

    interface Presenter {
        fun startPresenting()
        fun handleAddPlayerSelected()
    }
}