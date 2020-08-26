package com.cybershark.drawingsapp.util

sealed class UIState {
    object IDLE : UIState()
    object LOADING : UIState()
    data class ERROR(val message: String) : UIState()
    data class COMPLETED(val message: String) : UIState()
}