package com.gregmcgowan.fivesorgainser.playerlist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gregmcgowan.fivesorgainser.playerlist.PlayerListUserEvent.AddPlayerSelectedEvent
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.compose.ErrorMessage
import com.gregmcgowan.fivesorganiser.core.compose.Grey_300
import com.gregmcgowan.fivesorganiser.core.compose.Grey_400
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.compose.rememberFlowWithLifecycle
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState


@Composable
fun PlayerList(openImportContacts: () -> Unit) {
    val playerListViewModel = hiltViewModel<PlayerListViewModel>()

    // TODO can we avoiding having the same initial state here and the PlayerListViewModel
    val uiState by rememberFlowWithLifecycle(playerListViewModel.uiStateFlow)
            .collectAsState(initial = LoadingUiState())

    PlayerListScreen(
            uiState = uiState,
            eventHandler = { playerListUserEvent ->
                when (playerListUserEvent) {
                    AddPlayerSelectedEvent -> openImportContacts.invoke()
                }
            }
    )
}


@Composable
fun PlayerListScreen(
        uiState: UiState<PlayerListUiState>,
        eventHandler: (PlayerListUserEvent) -> Unit
) {

    when (uiState) {
        is LoadingUiState -> Loading()
        is ErrorUiState -> ErrorMessage(uiState)
        is ContentUiState -> PlayerListContent(uiState.content, eventHandler)
    }
}


@Composable
fun PlayerListContent(
        uiState: PlayerListUiState,
        eventHandler: (PlayerListUserEvent) -> Unit
) {
    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(text = stringResource(id = R.string.player_list_title))
                        }
                )
            },
            content = {
                if (uiState.players.isNotEmpty()) {
                    LazyColumn {
                        items(uiState.players) { player ->
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
                        onClick = { eventHandler.invoke(AddPlayerSelectedEvent) },
                        content = { Icon(Icons.Filled.Add, "") }
                )
            }
    )
}

@Composable
fun PlayerListItem(player: PlayerListItemUiState) {
    Column {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    PlayerAvatar()
                    Text(player.name, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp))
                }
        )
        Divider(color = Grey_400, modifier = Modifier.padding(start = 8.dp))
    }

}


@Composable
fun PlayerAvatar(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(48.dp)) {
        val canvasWidth: Float = size.width
        val canvasHeight: Float = size.height

        // main circle
        val mainCircleMargin: Float = 1.dp.toPx()
        val mainCircleRadius = canvasWidth.div(2f) - mainCircleMargin
        val middleX: Float = canvasWidth.div(2f)
        val middleY: Float = canvasHeight.div(2f)
        drawCircle(
                color = Grey_300,
                center = Offset(x = middleX, y = middleY),
                radius = mainCircleRadius
        )

        // head
        val headYOffset: Float = 4.dp.toPx()
        val headX: Float = middleX
        val headY: Float = middleY - headYOffset
        val headRadiusOffset: Float = 1.dp.value
        val headRatio = 3f
        val headRadius: Float = (mainCircleRadius / headRatio) + headRadiusOffset
        drawCircle(
                color = White,
                center = Offset(x = headX, y = headY),
                radius = headRadius
        )

        // body
        // clip outside the main circle
        val path = Path().apply {
            addOval(
                    Rect(Offset(x = mainCircleMargin, mainCircleMargin),
                            Size(width = mainCircleRadius.times(2),
                                    height = mainCircleRadius.times(2))
                    )
            )
        }
        clipPath(path) {
            val bodyHorizontalMargin = 8.dp.toPx()
            val neckMargin: Float = 4.dp.toPx()
            val bodyTop = headY + headRadius + neckMargin
            drawOval(
                    topLeft = Offset(x = bodyHorizontalMargin, y = bodyTop),
                    size = Size(
                            width = canvasWidth - (bodyHorizontalMargin * 2f),
                            height = canvasHeight - bodyTop
                    ),
                    color = White,
            )
            drawCircle(
                    color = Grey_300,
                    center = Offset(x = middleX, y = middleY),
                    radius = mainCircleRadius - (1.dp.toPx() / 2f),
                    style = Stroke(width = 1.dp.toPx())
            )
        }

    }
}

@Preview
@Composable
fun PlayerAvatarPreview() {
    PlayerAvatar()
}


@Preview
@Composable
fun PlayerListPreviewContent() {
    AppTheme {
        PlayerListScreen(
                ContentUiState(
                        content =
                        PlayerListUiState(
                                players =
                                listOf(
                                        PlayerListItemUiState("1", "reg"),
                                        PlayerListItemUiState("1", "frances"),
                                        PlayerListItemUiState("1", "reg"),
                                        PlayerListItemUiState("1", "reg")
                                )
                        )
                )
        ) {}
    }
}

@Preview
@Composable
fun PlayerListPreviewEmpty() {
    AppTheme {
        PlayerListScreen(
                ContentUiState(
                        content =
                        PlayerListUiState(players = emptyList())
                )
        ) {

        }
    }
}



