package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Added
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Modified
import com.gregmcgowan.fivesorganiser.data.DataChangeType.Removed
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.Before
import org.junit.Test

class PlayerListUiStateMapperImplTest {
    private lateinit var fixture: JFixture

    private lateinit var sut: PlayerListUiStateMapperImpl

    @Before
    fun setUp() {
        fixture = JFixture()
        sut = PlayerListUiStateMapperImpl()
    }

    @Test
    fun `map() adds players when there is none initially`() {
        val dataUpdate =
            DataUpdate(
                listOf(
                    DataChange(Added, createPlayer("Davie", "1")),
                    DataChange(Added, createPlayer("Greg", "2")),
                    DataChange(Added, createPlayer("Andy", "3")),
                ),
            )
        val existingModel = ContentUiState(PlayerListUiState(emptyList()))

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiState("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiState("1", "Davie")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiState("2", "Greg")))
    }

    @Test
    fun `map() removes an existing player`() {
        val dataUpdate = DataUpdate(listOf(DataChange(Removed, createPlayer("Davie", "1"))))

        val existingPlayerItems =
            listOf(
                PlayerListItemUiState("3", "Andy"),
                PlayerListItemUiState("1", "Davie"),
                PlayerListItemUiState("2", "Greg"),
            )

        val existingState = ContentUiState(PlayerListUiState(existingPlayerItems))

        val output = sut.map(existingState, dataUpdate)

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(2))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiState("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiState("2", "Greg")))
    }

    @Test
    fun `map() updates a modified player`() {
        val dataUpdate = DataUpdate(listOf(DataChange(Modified, createPlayer("Davie M", "1"))))

        val existingPlayerItems =
            listOf(
                PlayerListItemUiState("3", "Andy"),
                PlayerListItemUiState("1", "Davie"),
                PlayerListItemUiState("2", "Greg"),
            )

        val existingState = ContentUiState(PlayerListUiState(existingPlayerItems))

        val output = sut.map(existingState, dataUpdate)

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiState("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiState("1", "Davie M")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiState("2", "Greg")))
    }

    @Test
    fun `map() does not add a player an existing player twice`() {
        val dataUpdate =
            DataUpdate(
                listOf(
                    DataChange(Added, createPlayer("Davie", "1")),
                    DataChange(Added, createPlayer("Greg", "2")),
                    DataChange(Added, createPlayer("Andy", "3")),
                ),
            )
        val existingPlayerItems =
            listOf(
                PlayerListItemUiState("3", "Andy"),
                PlayerListItemUiState("1", "Davie"),
                PlayerListItemUiState("2", "Greg"),
            )

        val existingState = ContentUiState(PlayerListUiState(existingPlayerItems))

        val output = sut.map(existingState, dataUpdate)

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiState("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiState("1", "Davie")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiState("2", "Greg")))
    }

    @Test
    fun `map() sets error message when there are no players`() {
        val emptyUpdate = DataUpdate<Player>(emptyList())
        val existingState = ContentUiState(PlayerListUiState(emptyList()))

        val output = sut.map(existingState, emptyUpdate)

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(0))
    }

    private fun createPlayer(
        name: String,
        id: String,
    ): Player {
        return fixture.create(Player::class.java).copy(name = name, playerId = id)
    }
}
