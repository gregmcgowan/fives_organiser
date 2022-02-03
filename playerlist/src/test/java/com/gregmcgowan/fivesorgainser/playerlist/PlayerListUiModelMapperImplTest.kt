package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.ContentUiModel
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType.*
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.Before
import org.junit.Test

class PlayerListUiModelMapperImplTest {

    private lateinit var fixture: JFixture

    private lateinit var sut: PlayerListUiModelMapperImpl

    @Before
    fun setUp() {
        fixture = JFixture()
        sut = PlayerListUiModelMapperImpl()
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
        val existingModel = ContentUiModel(PlayerListUiModel(emptyList()))

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output, instanceOf(ContentUiModel::class.java))
        output as ContentUiModel
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiModel("1", "Davie")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() removes an existing player`() {
        val dataUpdate = DataUpdate(listOf(DataChange(Removed, createPlayer("Davie", "1"))))

        val existingPlayerModels = listOf(
                PlayerListItemUiModel("3", "Andy"),
                PlayerListItemUiModel("1", "Davie"),
                PlayerListItemUiModel("2", "Greg")
        )

        val existingModel = ContentUiModel(PlayerListUiModel(existingPlayerModels))

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output, instanceOf(ContentUiModel::class.java))
        output as ContentUiModel
        assertThat(output.content.players, hasSize(2))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() updates a modified player`() {
        val dataUpdate = DataUpdate(listOf(DataChange(Modified, createPlayer("Davie M", "1"))))

        val existingPlayerModels = listOf(
                PlayerListItemUiModel("3", "Andy"),
                PlayerListItemUiModel("1", "Davie"),
                PlayerListItemUiModel("2", "Greg")
        )

        val existingModel = ContentUiModel(PlayerListUiModel(existingPlayerModels))

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output, instanceOf(ContentUiModel::class.java))
        output as ContentUiModel
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiModel("1", "Davie M")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiModel("2", "Greg")))
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

        val existingModel = ContentUiModel(PlayerListUiModel(existingPlayerModels))

        val output = sut.map(existingModel, dataUpdate)

        assertThat(output, instanceOf(ContentUiModel::class.java))
        output as ContentUiModel
        assertThat(output.content.players, hasSize(3))
        assertThat(output.content.players[0], equalTo(PlayerListItemUiModel("3", "Andy")))
        assertThat(output.content.players[1], equalTo(PlayerListItemUiModel("1", "Davie")))
        assertThat(output.content.players[2], equalTo(PlayerListItemUiModel("2", "Greg")))
    }

    @Test
    fun `map() sets error message when there are no players`() {
        val emptyUpdate = DataUpdate<Player>(emptyList())
        val existingModel = ContentUiModel(PlayerListUiModel(emptyList()))

        val output = sut.map(existingModel, emptyUpdate)

        assertThat(output, instanceOf(ContentUiModel::class.java))
        output as ContentUiModel
        assertThat(output.content.players, hasSize(0))
    }

    private fun createPlayer(name: String, id: String): Player {
        return fixture.create(Player::class.java).copy(name = name, playerId = id)
    }

}