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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.json.JSONObject

class GuessHints : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadImageFromDrawable(this)
        }
    }
}


@Composable
fun LoadImageFromDrawable(context: Context) {
    val countriesMap = loadCountriesFromJson1(context)
    // Use a state to trigger recomposition
    var triggerRecomposition by remember { mutableStateOf(false) }

    // Key here is to make sure recomposition is triggered, affecting the selection
    val (countryCode, countryName) = remember(triggerRecomposition) { countriesMap.entries.random() }

    val resourceId = remember(countryCode) {
        context.resources.getIdentifier(countryCode.lowercase(), "drawable", context.packageName)
    }
    val imagePainter = if (resourceId != 0) painterResource(id = resourceId) else null

    var displayCountryName by remember(countryName) { mutableStateOf("_".repeat(countryName.length)) }
    var userGuess by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var attemptsLeft by remember { mutableStateOf(3) }
    var roundOver by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (imagePainter != null) {
            Image(
                painter = imagePainter,
                contentDescription = "Flag of $countryName",
                modifier = Modifier.size(300.dp).padding(top = 16.dp)
            )
            // Display the country name for testing or educational purposes
            Text(text = "Flag of $countryName", modifier = Modifier.padding(bottom = 8.dp))
            Text(text = displayCountryName)
            Text(text = "Attempts left: $attemptsLeft")
        } else {
            Text("Flag image not found")
        }

        OutlinedTextField(
            value = userGuess,
            onValueChange = { userGuess = it.lowercase() },
            label = { Text("Enter your guess") },
            singleLine = true,
            modifier = Modifier.padding(16.dp)
        )

        Button(onClick = {
            if (!roundOver) {
                if (userGuess.isNotEmpty() && userGuess.length == 1) {
                    if (countryName.lowercase().contains(userGuess)) {
                        displayCountryName = displayCountryName.toCharArray().apply {
                            countryName.forEachIndexed { index, c ->
                                if (c.lowercase() == userGuess) this[index] = c
                            }
                        }.concatToString()

                        if (!displayCountryName.contains('_')) {
                            feedbackMessage = "Correct! The country is $countryName"
                            roundOver = true
                        }
                    } else {
                        attemptsLeft--
                        if (attemptsLeft <= 0) {
                            feedbackMessage = "Wrong! The correct country was $countryName"
                            roundOver = true
                        } else {
                            feedbackMessage = "Incorrect, try again! Attempts left: $attemptsLeft"
                        }
                    }
                    userGuess = ""
                }
            } else {
                // Reset for a new game round by toggling the trigger
                triggerRecomposition = !triggerRecomposition
                attemptsLeft = 3
                feedbackMessage = ""
                roundOver = false
                userGuess = ""
            }
        }) {
            Text(if (roundOver) "Next" else "Submit")
        }

        Text(text = feedbackMessage, color = when {
            feedbackMessage.startsWith("Correct") -> Color.Green
            feedbackMessage.startsWith("Wrong") || feedbackMessage.startsWith("Incorrect") -> Color.Red
            else -> Color.Black
        })
    }
}






fun loadCountriesFromJson1(context: Context): Map<String, String> {
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val countriesMap = mutableMapOf<String, String>()
    jsonObject.keys().forEach { key ->
        countriesMap[key] = jsonObject.getString(key)
    }
    return countriesMap
}



