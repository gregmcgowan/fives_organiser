package com.gregmcgowan.fivesorganiser.main

typealias MainScreenUiModelReducer = (MainScreenUiModel) -> MainScreenUiModel

internal fun authCompleteUiModel(selectedScreen: MainScreen)
        : MainScreenUiModelReducer = { model ->
    mainScreenUiModel(selectedScreen, model.copy(showLoading = false, showContent = true))
}

internal fun menuSelectedUiModel(selectedScreen: MainScreen): MainScreenUiModelReducer = { model ->
    mainScreenUiModel(selectedScreen, model)
}

private fun mainScreenUiModel(selectedScreen: MainScreen,
                               model: MainScreenUiModel): MainScreenUiModel {
    return model.copy(
            screenToShow = selectedScreen,
            showContent = true,
            showLoading = false

    )
}