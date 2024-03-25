package com.example.asd

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject


class GuessHints : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timerEnabled = intent.getBooleanExtra("timerEnabled", false)
        setContent {
            DisplayCountryFlag(this, timerEnabled)
        }
    }
}

@Composable
fun DisplayCountryFlag(context: Context, timerEnabled: Boolean = false) {
    val countries = loadCountriesFromJson2(context)
    var refresh by remember { mutableStateOf(false) }

    val (code, name) = remember(refresh) { countries.entries.random() }
    val drawableId = remember(code) {
        context.resources.getIdentifier(code.lowercase(), "drawable", context.packageName)
    }
    val flagPainter = if (drawableId != 0) painterResource(id = drawableId) else null

    var displayedName by remember(name) { mutableStateOf("_".repeat(name.length)) }
    var guess by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var guessesRemaining by remember { mutableStateOf(3) }
    var gameEnded by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(90) }
    var score by remember { mutableStateOf(0) } // Score variable

    if (timerEnabled && !gameEnded) {
        LaunchedEffect(refresh) {
            timeLeft = 90 // Reset the timer
            repeat(90) {
                delay(1000)
                timeLeft -= 1
                if (timeLeft <= 0) {
                    guessesRemaining -= 1
                    if (guessesRemaining <= 0) {
                        message = "Time's up! Game over! It was $name"
                        gameEnded = true
                    } else {
                        message = "Time's up! Try again! $guessesRemaining guesses left"
                    }
                    timeLeft = 90 // Reset for the next round if necessary
                    refresh = !refresh // Trigger new flag if the game continues
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (timerEnabled) {
            Text("Time left: $timeLeft seconds", Modifier.padding(8.dp))
        }

        Text("Score: $score", Modifier.padding(8.dp)) // Display the score

        if (flagPainter != null) {
            Image(
                painter = flagPainter,
                contentDescription = "Flag of $name",
                modifier = Modifier.size(300.dp).padding(top = 16.dp)
            )
            Text("Flag of $name", Modifier.padding(bottom = 8.dp))
            Text(displayedName)
            Text("Guesses left: $guessesRemaining")
        } else {
            Text("Flag image not found")
        }

        OutlinedTextField(
            value = guess,
            onValueChange = { guess = it.lowercase() },
            label = { Text("Your guess") },
            singleLine = true,
            modifier = Modifier.padding(16.dp)
        )

        Button(onClick = {
            if (!gameEnded) {
                if (guess.isNotEmpty() && guess.length == 1) {
                    if (name.lowercase().contains(guess)) {
                        displayedName = displayedName.mapIndexed { index, char ->
                            if (name[index].lowercase() == guess) name[index] else char
                        }.joinToString("")

                        if ('_' !in displayedName) {
                            message = "Correct! It's $name"
                            score += 1 // Increase score for correct guess
                            gameEnded = true
                        }
                    } else {
                        guessesRemaining--
                        if (guessesRemaining <= 0) {
                            gameEnded = true
                            message = "Game over! It was $name"
                        } else {
                            message = "Try again! $guessesRemaining guesses left"
                        }
                    }
                    guess = ""
                }
            } else {
                refresh = !refresh
                guessesRemaining = 3
                message = ""
                gameEnded = false
                guess = ""
                displayedName = "_".repeat(name.length) // Reset displayed name for new flag
            }
        }) {
            Text(if (gameEnded) "Next" else "Guess")
        }

        Text(text = message, color = when {
            message.startsWith("Correct") -> Color.Green
            message.contains("over") || message.startsWith("Try again") -> Color.Red
            else -> Color.Black
        })
    }
}



fun loadCountriesFromJson2(context: Context): Map<String, String> {
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val countriesMap = mutableMapOf<String, String>()
    jsonObject.keys().forEach { key ->
        countriesMap[key] = jsonObject.getString(key)
    }
    return countriesMap
}

