package com.example.asd

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(onClick = {
                    val guessTheCountry = Intent(this@MainActivity, GuessTheCountry::class.java)
                    startActivity(guessTheCountry)
                }) {
                    Text(text = "Guess the Country", fontSize = 18.sp)

                }

                Button(onClick = {
                    val guessHints = Intent(this@MainActivity, GuessHints::class.java)
                    startActivity(guessHints)
                }) {
                    Text(text = "Guess Hints", fontSize = 18.sp)

                }


                Button(onClick = {
                    val guessTheFlag = Intent(this@MainActivity, GuessTheFlag::class.java)
                    startActivity(guessTheFlag)
                }) {
                    Text(text = "Guess The Flag", fontSize = 18.sp)

                }

                Button(onClick = {
                    val advancedLevel = Intent(this@MainActivity, AdvancedLevel::class.java)
                    startActivity(advancedLevel)
                }) {
                    Text(text = "Advanced Level", fontSize = 18.sp)

                }

            }
        }
    }
}

