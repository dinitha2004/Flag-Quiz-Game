package com.example.asd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.asd.ui.theme.AsdTheme
import android.content.Context
import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import kotlin.random.Random

class GuessTheCountry : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timerEnabled = intent.getBooleanExtra("timerEnabled", false)
        setContent {
            AsdTheme {
                // Pass the context to the guessingTheCountry function
                guessingTheCountry(this,timerEnabled)
            }
        }
    }
}

@Composable
fun guessingTheCountry(context: Context, timerEnabled: Boolean) {
    val countries = loadCountriesFromJson(context)
    val countryCodes = countries.keys.toList()
    var randomIndex by rememberSaveable { mutableStateOf(Random.nextInt(countryCodes.size)) }
    var answer by rememberSaveable { mutableStateOf("") }
    var feedbackMessage by rememberSaveable { mutableStateOf("") }
    var submitLabel by rememberSaveable { mutableStateOf("Submit") }
    var timeLeft by rememberSaveable { mutableStateOf(10) }

    val randomCountryCode = countryCodes[randomIndex].toLowerCase()
    val randomCountryName = countries[randomCountryCode]?.capitalize() ?: "Unknown"

    LaunchedEffect(key1 = timerEnabled, key2 = randomIndex) {
        if (timerEnabled) {
            val timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                    feedbackMessage = if (answer == randomCountryCode) {
                        "Correct! It's ${countries[randomCountryCode]}."
                    } else {
                        "Incorrect. The correct answer is ${countries[randomCountryCode]}."
                    }
                    submitLabel = "Next"
                    timeLeft = 10 // Reset timer for next question
                }
            }
            timer.start()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (timerEnabled) {
            Text(
                text = "Time left: $timeLeft",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 18.sp
            )
        }
        val imageId = context.resources.getIdentifier(randomCountryCode, "drawable", context.packageName)
        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Flag of $randomCountryName",
                modifier = Modifier.size(200.dp).padding(16.dp)
            )
        } else {
            Text("Flag image not found for $randomCountryCode", color = Color.Red)
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            items(items = countryCodes) { code ->
                CountryItem(countryName = countries[code] ?: "", selectedCountry = answer) {
                    answer = code
                }
            }
        }

        if (feedbackMessage.isNotEmpty()) {
            Text(feedbackMessage, modifier = Modifier.padding(8.dp), color = if (answer == randomCountryCode) Color.Green else Color.Red)
        }

        Button(
            onClick = {
                if (submitLabel == "Submit") {
                    feedbackMessage = if (answer == randomCountryCode) {
                        "Correct! It's ${countries[randomCountryCode]}."
                    } else {
                        "Incorrect. The correct answer is ${countries[randomCountryCode]}."
                    }
                    submitLabel = "Next"
                } else {
                    randomIndex = Random.nextInt(countryCodes.size)
                    feedbackMessage = ""
                    submitLabel = "Submit"
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(submitLabel)
        }
    }
}

@Composable
fun CountryItem(countryName: String, selectedCountry: String, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = countryName,
            fontSize = 18.sp,
            color = if (countryName.lowercase() == selectedCountry.lowercase()) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}


fun loadCountriesFromJson(context: Context): Map<String, String> {
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val countriesMap = mutableMapOf<String, String>()
    jsonObject.keys().forEach { key ->
        countriesMap[key.lowercase()] = jsonObject.getString(key).capitalize()
    }
    return countriesMap
}