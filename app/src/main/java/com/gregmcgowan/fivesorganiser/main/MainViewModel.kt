package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.authenication.Authentication
import com.gregmcgowan.fivesorganiser.core.ui.NonNullMutableLiveData
import kotlin.coroutines.experimental.CoroutineContext

class MainViewModel(ui: CoroutineContext,
                    background: CoroutineContext,
                    private val authentication: Authentication) : CoroutinesViewModel(ui, background) {

    private var currentScreen: MainScreen = MainScreen.PlayersScreen

    private val uiModelLiveData = NonNullMutableLiveData(
            MainScreenUiModel(
                    screenToShow = currentScreen,
                    showContent = false,
                    showLoading = true
            )
    )

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
        uiModelLiveData.value = reducer.invoke(uiModelLiveData.getNonNullValue())
    }

    fun handleMenuSelection(selectedScreen: MainScreen) {
        currentScreen = selectedScreen
        updateUiModel(menuSelectedUiModel(selectedScreen))
    }
}
