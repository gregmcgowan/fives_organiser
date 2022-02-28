package com.gregmcgowan.fivesorganiser.core.ui

sealed class UiState<T> {

    class LoadingUiState<T> : UiState<T>()

    class ErrorUiState<T>(val string: Int) : UiState<T>()

    class ContentUiState<T>(val content: T) : UiState<T>()

}
