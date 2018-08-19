package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val mainUiModelMapper: MainUiModelMapper,
        private val authentication: Authentication,
        coroutineContext: CoroutineContexts
) : CoroutinesViewModel(coroutineContext) {

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
    }

    fun onViewCreated() {
        runOnBackgroundAndUpdateOnUI(
                { authentication.initialise() },
                { _ -> updateUiModel(currentScreen) }
        )
    }

    private fun updateUiModel(selectedScreen: MainScreen) {
        _uiModelLiveData.value = mainUiModelMapper.map(selectedScreen)
    }

    fun handleMenuSelection(selectedScreen: MainScreen) {
        currentScreen = selectedScreen
        updateUiModel(selectedScreen)
    }
}
