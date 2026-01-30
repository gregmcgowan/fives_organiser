package com.gregmcgowan.fivesorganiser.core.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gregmcgowan.fivesorganiser.core.ui.UiState

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun <T> ErrorMessage(
    errorUiState: UiState.ErrorUiState<T>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
    ) {
        Text(stringResource(id = errorUiState.string))
    }
}
