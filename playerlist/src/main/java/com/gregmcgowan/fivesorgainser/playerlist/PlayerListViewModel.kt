package com.gregmcgowan.fivesorgainser.playerlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.LoadingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
        private val uiModelMapper: PlayerListUiModelMapper,
        getPlayerListUpdatesUseCase: GetPlayerListUpdatesUseCase
) : ViewModel() {

    var uiModel: UiModel<PlayerListUiModel> by mutableStateOf(value = LoadingUiModel())
        private set

    init {
        viewModelScope.launch {
            try {
                getPlayerListUpdatesUseCase
                        .execute()
                        .collect {
                            uiModel = uiModelMapper.map(uiModel, it)
                        }
            } catch (exception: Exception) {
                handleError(exception)
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        uiModel = UiModel.ErrorUiModel(string = R.string.generic_error_message)
    }

}
