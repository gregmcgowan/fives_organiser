package com.gregmcgowan.fivesorganiser.core.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
                .background(MaterialTheme.colorScheme.surface)
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
                .background(MaterialTheme.colorScheme.surface)
                .wrapContentSize(Alignment.Center),
    ) {
        Text(stringResource(id = errorUiState.string))
    }
}
