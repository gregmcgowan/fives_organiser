package com.gregmcgowan.fivesorganiser.main

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.compose.Green_500
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen(modifier = Modifier.safeDrawingPadding())
            }
        }
    }
}
