package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.gregmcgowan.fivesorganiser.Dependencies
import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.main.matchList.MatchListViewModel
import com.gregmcgowan.fivesorganiser.main.playerList.PlayerListViewModel
import kotlin.coroutines.experimental.CoroutineContext

class MainViewModelFactory(dependencies: Dependencies,
                           uiContext: CoroutineContext,
                           backgroundContext: CoroutineContext) : ViewModelProvider.NewInstanceFactory() {

    private val viewModelMap: MutableMap<Class<*>, ViewModel> = mutableMapOf()

    init {
        viewModelMap.put(PlayerListViewModel::class.java,
                PlayerListViewModel(uiContext, backgroundContext, dependencies.playersRepo))
        viewModelMap.put(MatchListViewModel::class.java,
                MatchListViewModel(uiContext, backgroundContext, dependencies.matchRepo,
                        ZonedDateTimeFormatter()))
        viewModelMap.put(MainViewModel::class.java,
                MainViewModel(uiContext, backgroundContext, dependencies.authentication))
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return viewModelMap[modelClass] as T
    }
}