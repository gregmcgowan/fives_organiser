package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType.*
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.Before
import org.junit.Test

class PlayerListUiModelMapperTest {

    private lateinit var fixture: JFixture

    private lateinit var sut: PlayerListUiModelMapper

    @Before
    fun setUp() {
        fixture = JFixture()
        sut = PlayerListUiModelMapper()
    }

    @Test
    fun `map() adds players when there is none initially`() {
        val dataUpdate = DataUpdate(
                listOf(
                        DataChange(Added, createPlayer("Davie", "1")),
                        DataChange(Added, createPlayer("Greg", "2")),
                        DataChange(Added, createPlayer("Andy", "3"))
                )
        )
        val existingModel = PlayerListUiModel(emptyList(), false, true, false, NO_STRING_RES_ID)

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output.showPlayers, equalTo(true))
        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showErrorMessage, equalTo(false))
        assertThat(output.errorMessage, equalTo(NO_STRING_RES_ID))
        assertThat(output.players, hasSize(3))
        assertThat(output.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.players[1], equalTo(PlayerListItemUiModel("1", "Davie")))
        assertThat(output.players[2], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() removes an existing player`() {
        val dataUpdate = DataUpdate(listOf(DataChange(Removed, createPlayer("Davie", "1"))))

        val existingPlayerModels = listOf(
                PlayerListItemUiModel("3", "Andy"),
                PlayerListItemUiModel("1", "Davie"),
                PlayerListItemUiModel("2", "Greg")
        )

        val existingModel = PlayerListUiModel(existingPlayerModels, true, true, false, NO_STRING_RES_ID)

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output.showPlayers, equalTo(true))
        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showErrorMessage, equalTo(false))
        assertThat(output.errorMessage, equalTo(NO_STRING_RES_ID))
        assertThat(output.players, hasSize(2))
        assertThat(output.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.players[1], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() updates a modified player`() {
        val dataUpdate = DataUpdate(listOf(DataChange(Modified, createPlayer("Davie M", "1"))))

        val existingPlayerModels = listOf(
                PlayerListItemUiModel("3", "Andy"),
                PlayerListItemUiModel("1", "Davie"),
                PlayerListItemUiModel("2", "Greg")
        )

        val existingModel = PlayerListUiModel(existingPlayerModels, true, true, false, NO_STRING_RES_ID)

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output.showPlayers, equalTo(true))
        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showErrorMessage, equalTo(false))
        assertThat(output.errorMessage, equalTo(NO_STRING_RES_ID))
        assertThat(output.players, hasSize(3))
        assertThat(output.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.players[1], equalTo(PlayerListItemUiModel("1", "Davie M")))
        assertThat(output.players[2], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() does not add a player an existing player twice`() {
        val dataUpdate = DataUpdate(
                listOf(
                        DataChange(Added, createPlayer("Davie", "1")),
                        DataChange(Added, createPlayer("Greg", "2")),
                        DataChange(Added, createPlayer("Andy", "3"))
                )
        )
        val existingPlayerModels = listOf(
                PlayerListItemUiModel("3", "Andy"),
                PlayerListItemUiModel("1", "Davie"),
                PlayerListItemUiModel("2", "Greg")
        )

        val existingModel = PlayerListUiModel(existingPlayerModels, true, true, false, NO_STRING_RES_ID)

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output.showPlayers, equalTo(true))
        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showErrorMessage, equalTo(false))
        assertThat(output.errorMessage, equalTo(NO_STRING_RES_ID))
        assertThat(output.players, hasSize(3))
        assertThat(output.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.players[1], equalTo(PlayerListItemUiModel("1", "Davie")))
        assertThat(output.players[2], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() sets error message when there are no players`() {
        val emptyUpdate = DataUpdate<Player>(emptyList())
        val existingModel = PlayerListUiModel(emptyList(), true, true, false, NO_STRING_RES_ID)

        val output = sut.map(existingModel, emptyUpdate)

        assertThat(output.showPlayers, equalTo(false))
        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showErrorMessage, equalTo(true))
        assertThat(output.errorMessage, equalTo(R.string.player_list_no_players_message))
        assertThat(output.players, hasSize(0))
    }

    private fun createPlayer(name: String, id: String): Player {
        return fixture.create(Player::class.java).copy(name = name, playerId = id)
    }

}