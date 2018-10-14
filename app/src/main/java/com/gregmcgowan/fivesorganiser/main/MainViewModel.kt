package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.Dispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val mapper: MainUiModelMapper,
        private val authentication: Authentication,
        dispatchers: Dispatchers
) : CoroutinesViewModel(dispatchers) {

    val uiModelLiveData: LiveData<MainScreenUiModel>
        get() = _uiModelLiveData

    private val _uiModelLiveData: MutableLiveData<MainScreenUiModel> = MutableLiveData()

    private var currentScreen: MainScreen = MainScreen.PlayersScreen

    init {
        _uiModelLiveData.value = MainScreenUiModel(
                screenToShow = currentScreen,
                showContent = false,
                showLoading = true
        )

        launch(
                backgroundBlock = { authentication.initialise() },
                uiBlock = { updateUiModel(currentScreen) })
    }

    private fun updateUiModel(selectedScreen: MainScreen) {
        _uiModelLiveData.value = mapper.map(selectedScreen)
    }

    fun handleMenuSelection(selectedScreen: MainScreen) {
        currentScreen = selectedScreen
        updateUiModel(selectedScreen)
    }
}
