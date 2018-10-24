package com.gregmcgowan.fivesorganiser.matchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.Dispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.data.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.*
import timber.log.Timber
import javax.inject.Inject

class MatchListViewModel @Inject constructor(
        private val matchInteractor: MatchInteractor,
        private val mapper: MatchListUiModelMappers,
        dispatchers: Dispatchers
) : CoroutinesViewModel(dispatchers) {

    val matchListUiModelLiveData: LiveData<MatchListUiModel>
        get() = _matchListUiModelLiveData

    val navigationLiveData: LiveData<MatchNavigationEvent>
        get() = _navigationLiveData

    private val _matchListUiModelLiveData = MutableLiveData<MatchListUiModel>()

    private val _navigationLiveData = MutableLiveData<MatchNavigationEvent>()

    init {
        _matchListUiModelLiveData.value = MatchListUiModel(
                showList = false,
                showProgressBar = true,
                showEmptyView = false,
                emptyMessage = null,
                matches = emptyList()
        )

        _navigationLiveData.value = Idle
    }


    fun onViewShown() {
        _navigationLiveData.value = Idle

        updateUiModel(
                _matchListUiModelLiveData.requireValue().copy(
                        showList = false,
                        showProgressBar = true,
                        showEmptyView = false
                )
        )

        launch(
                backgroundBlock = { mapper.map(matchInteractor.getAllMatches()) },
                uiBlock = { uiModel -> updateUiModel(uiModel) }
        )
    }

    private fun updateUiModel(model: MatchListUiModel) {
        Timber.d("Setting match list UI model to ${_matchListUiModelLiveData.value}")
        _matchListUiModelLiveData.value = model

    }

    fun addMatchButtonPressed() {
        _navigationLiveData.value = StartNewMatchFlow
    }

    fun editMatchDetails(matchId: String) {
        _navigationLiveData.value = ShowMatchDetails(matchId)
    }

    fun editSquad(matchId: String) {
        _navigationLiveData.value = ShowSquad(matchId)
    }

}