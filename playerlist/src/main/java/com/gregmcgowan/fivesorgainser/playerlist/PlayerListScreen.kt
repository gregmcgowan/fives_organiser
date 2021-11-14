package com.gregmcgowan.fivesorgainser.playerlist


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.gregmcgowan.fivesorganiser.core.compose.ErrorMessage
import com.gregmcgowan.fivesorganiser.core.compose.Grey_400
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.ui.PlayerAvatar
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.*


@Composable
fun PlayerListScreen(
        uiModel: UiModel<PlayerListUiModel>,
        eventHandler: (PlayerListUserEvent) -> Unit
) {

    when (uiModel) {
        is LoadingUiModel -> Loading()
        is ErrorUiModel -> ErrorMessage(uiModel)
        is ContentUiModel -> PlayerListContent(uiModel.content, eventHandler)
    }
}


@Composable
fun PlayerListContent(
        uiModel: PlayerListUiModel,
        eventHandler: (PlayerListUserEvent) -> Unit
) {
    Scaffold(
            content = {
                if (uiModel.players.isNotEmpty()) {
                    LazyColumn {
                        items(uiModel.players) { player ->
                            PlayerListItem(player)
                        }
                    }
                } else {
                    Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                Image(
                                        modifier = Modifier.size(256.dp),
                                        painter = painterResource(id = R.drawable.peter_beardsley),
                                        contentDescription = null
                                )
                                Text(
                                        stringResource(id = R.string.player_list_no_players_message),
                                        fontSize = 24.sp
                                )
                            }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                        onClick = { eventHandler.invoke(PlayerListUserEvent.AddPlayerSelectedEvent) },
                        content = { Icon(Icons.Filled.Add, "") }
                )
            }
    )
}

@Composable
fun PlayerListItem(player: PlayerListItemUiModel) {
    Column {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    AndroidView(
                            factory = { context -> PlayerAvatar(context) },
                            update = {},
                            modifier = Modifier
                                    .size(48.dp)
                                    .padding(start = 16.dp, top = 8.dp)
                    )
                    Text(player.name, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp))
                }
        )
        Divider(color = Grey_400, modifier = Modifier.padding(start = 8.dp))
    }

}

@Preview
@Composable
fun PlayerListPreviewContent() {
    PlayerListScreen(
            ContentUiModel(
                    content =
                    PlayerListUiModel(
                            players =
                            listOf(
                                    PlayerListItemUiModel("1", "reg"),
                                    PlayerListItemUiModel("1", "reg"),
                                    PlayerListItemUiModel("1", "reg"),
                                    PlayerListItemUiModel("1", "reg")
                            )
                    )
            )
    ) {

    }
}

@Preview
@Composable
fun PlayerListPreviewEmpty() {
    PlayerListScreen(
            ContentUiModel(
                    content =
                    PlayerListUiModel(players = emptyList())
            )
    ) {

    }
}


