package com.gregmcgowan.fivesorganiser.main

import javax.inject.Inject

class MainUiModelMapper @Inject constructor() {

    // TODO probably don't need this
    fun map(selectedScreen: MainScreen) = MainScreenUiModel(
            screenToShow = selectedScreen,
            showContent = true,
            showLoading = false
    )
}

