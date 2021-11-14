package com.gregmcgowan.fivesorganiser.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        private val mapper: MainUiModelMapper,
        private val authentication: Authentication,
        coroutineDispatchers: CoroutineDispatchers
) : CoroutinesViewModel(coroutineDispatchers) {

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
                uiBlock = { updateUiModel(currentScreen) }
        )
    }

    private fun updateUiModel(selectedScreen: MainScreen) {
        _uiModelLiveData.value = mapper.map(selectedScreen)
    }

    fun handleMenuSelection(selectedScreen: MainScreen) {
        currentScreen = selectedScreen
        updateUiModel(selectedScreen)
    }
}
