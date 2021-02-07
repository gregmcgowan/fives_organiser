package com.gregmcgowan.fivesorganiser.core.ui

sealed class UiModel<T> {

    class LoadingUiModel<T> : UiModel<T>()

    class ErrorUiModel<T>(val string: Int) : UiModel<T>()

    class ContentUiModel<T>(val content: T) : UiModel<T>()

}
