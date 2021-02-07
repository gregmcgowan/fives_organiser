package com.gregmcgowan.fivesorgainser.playerlist

import androidx.compose.runtime.Composable
import com.gregmcgowan.fivesorganiser.core.compose.ErrorMessage
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.*


@Composable
fun PlayerListScreen(uiModel: UiModel<PlayerListUiModel>,
                     eventHandler: (PlayerListUserEvent) -> Unit) {
    when(uiModel) {
        is LoadingUiModel -> Loading()
        is ErrorUiModel -> ErrorMessage(uiModel)
        is ContentUiModel -> PlayerListContent(uiModel,eventHandler)
    }
}

@Composable
fun PlayerListContent(uiModel: ContentUiModel<PlayerListUiModel>,
                      eventHandler: (PlayerListUserEvent) -> Unit) {
    TODO("Not yet implemented")
}

