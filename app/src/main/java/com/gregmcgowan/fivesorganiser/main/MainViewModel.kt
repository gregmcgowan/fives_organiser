package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.requireValue
import javax.inject.Inject

class MainViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val authentication: Authentication
) : CoroutinesViewModel(coroutineContext) {

    private var currentScreen: MainScreen = MainScreen.PlayersScreen

    var uiModelLiveData: MutableLiveData<MainScreenUiModel> = MutableLiveData()

    init {
        uiModelLiveData.value = MainScreenUiModel(
                screenToShow = currentScreen,
                showContent = false,
                showLoading = true
        )
    }

    fun onViewCreated() {
        runOnBackgroundAndUpdateOnUI(
                { authentication.initialise() },
                { _ -> updateUiModel(authCompleteUiModel(currentScreen)) }
        )
    }

    fun uiModelLiveData(): LiveData<MainScreenUiModel> {
        return uiModelLiveData
    }

    private fun updateUiModel(reducer: MainScreenUiModelReducer) {
        uiModelLiveData.value = reducer.invoke(uiModelLiveData.requireValue())
    }

    fun handleMenuSelection(selectedScreen: MainScreen) {
        currentScreen = selectedScreen
        updateUiModel(menuSelectedUiModel(selectedScreen))
    }
}
