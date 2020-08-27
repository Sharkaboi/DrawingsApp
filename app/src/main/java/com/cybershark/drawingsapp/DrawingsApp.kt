package com.cybershark.drawingsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DrawingsApp : Application(){
    companion object{
        const val FolderName = "Drawings"
    }
}