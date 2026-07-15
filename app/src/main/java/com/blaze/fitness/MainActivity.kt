package com.blaze.fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.blaze.fitness.ui.navigation.BlazeNavGraph
import com.blaze.fitness.ui.theme.BlazeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as BlazeApplication).container

        setContent {
            BlazeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BlazeNavGraph(container = container)
                }
            }
        }
    }
}
