package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
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
    fun `map() maps players`() {
        val players =
            listOf(
                createPlayer("Andy", "3"),
                createPlayer("Davie", "1"),
                createPlayer("Greg", "2"),
            )

        val output = sut.map(players)

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiState("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiState("1", "Davie")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiState("2", "Greg")))
    }

    @Test
    fun `map() no players`() {
        val output = sut.map(emptyList())

        assertThat(output, instanceOf(ContentUiState::class.java))
        output as ContentUiState
        assertThat(output.content.players, hasSize(0))
    }

    private fun createPlayer(
        name: String,
        id: String,
    ): Player = fixture.create(Player::class.java).copy(name = name, playerId = id)
}
