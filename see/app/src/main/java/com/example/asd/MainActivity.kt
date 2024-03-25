package com.example.asd

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val context = LocalContext.current // Get the current context

    // Track the switch state
    var timerEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val intent = Intent(context, GuessTheCountry::class.java).apply {
                putExtra("timerEnabled", timerEnabled)
            }
            context.startActivity(intent)
        }) {
            Text(text = "Guess the Country", fontSize = 18.sp)
        }

        // Repeat for other buttons, using `context` instead of `this@MainActivity`
        Button(onClick = {
            val intent = Intent(context, GuessHints::class.java).apply {
                putExtra("timerEnabled", timerEnabled)
            }
            context.startActivity(intent)
        }) {
            Text(text = "Guess Hints", fontSize = 18.sp)
        }

        Button(onClick = {
            val intent = Intent(context, GuessTheFlag::class.java).apply {
                putExtra("timerEnabled", timerEnabled)
            }
            context.startActivity(intent)
        }) {
            Text(text = "Guess The Flag", fontSize = 18.sp)
        }

        Button(onClick = {
            val intent = Intent(context, AdvancedLevel::class.java).apply {
                putExtra("timerEnabled", timerEnabled)
            }
            context.startActivity(intent)
        }) {
            Text(text = "Advanced Level", fontSize = 18.sp)
        }

        // Timer switch
        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Enable Timer", modifier = Modifier.padding(end = 8.dp))
            Switch(
                checked = timerEnabled,
                onCheckedChange = { timerEnabled = it }
            )
        }
    }
}



