package com.gregmcgowan.fivesorganiser.main

import javax.inject.Inject

class  MainUiModelMapper @Inject constructor() {

    fun map(selectedScreen: MainScreen): MainScreenUiModel = MainScreenUiModel(
            screenToShow = selectedScreen,
            showContent = true,
            showLoading = false
    )
}

