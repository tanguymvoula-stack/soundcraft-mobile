package com.soundcraft.studio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.soundcraft.studio.ui.StudioScreen
import com.soundcraft.studio.ui.StudioTheme

class MainActivity : ComponentActivity() {
    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudioTheme { StudioScreen(viewModel) }
        }
    }

    override fun onStop() {
        // On libère la sortie audio dès que l'app quitte l'écran.
        viewModel.onBackgrounded()
        super.onStop()
    }
}
