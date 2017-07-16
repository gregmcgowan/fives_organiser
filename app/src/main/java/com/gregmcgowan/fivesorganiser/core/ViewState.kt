package com.gregmcgowan.fivesorganiser.core

sealed class ViewState {
    class Loading : ViewState()
    class Success<T>( val item: T) : ViewState()
    class Error(val errorMessage: String?) : ViewState()
}

