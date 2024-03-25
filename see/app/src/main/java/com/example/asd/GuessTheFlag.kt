package com.example.asd

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asd.ui.theme.AsdTheme
import org.json.JSONObject

class GuessTheFlag : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val timerEnabled = intent.getBooleanExtra("timerEnabled", false) // Receive the flag
            val countriesMap by remember { mutableStateOf(loadCountriesJson(context)) }
            FlagQuiz(countriesMap, timerEnabled) // Pass the flag to the composable
        }
    }
}



@Composable
fun FlagQuiz(countries: Map<String, String>, timerEnabled: Boolean) {
    val context = LocalContext.current
    var timeLeft by remember { mutableStateOf(10) } // Timer countdown from 10 seconds
    var flagOptions by remember { mutableStateOf(listOf<String>()) }
    var answerKey by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(Color.Black) }
    var canGuess by remember { mutableStateOf(true) }

    // Define the startTimer function
    fun startTimer() {
        if (timerEnabled) {
            object : CountDownTimer(10000, 1000) { // 10 seconds, ticking every second
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                    feedback = "TIME'S UP! The correct answer was: ${countries[answerKey]}"
                    feedbackColor = Color.Red
                    canGuess = false
                }
            }.start()
        }
    }

    LaunchedEffect(key1 = "initialize") {
        flagOptions = countries.keys.shuffled().take(3)
        answerKey = flagOptions.random()
        startTimer() // Start the timer
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show the timer if enabled
        if (timerEnabled) {
            Text("Time left: $timeLeft seconds",
                color = if (timeLeft <= 3) Color.Red else Color.Black, // Change color when time is running out
                fontSize = 18.sp)
        }
        Text("Identify the flag of: ${countries[answerKey]}",
            fontSize = 17.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            modifier = Modifier.padding(vertical = 16.dp))

        flagOptions.forEach { countryCode ->
            val drawableId = context.resources.getIdentifier(countryCode.toLowerCase(), "drawable", context.packageName)
            if (drawableId != 0) { // Ensure the resource exists
                Button(
                    onClick = {
                        if (canGuess) {
                            feedback = if (countryCode == answerKey) "CORRECT!" else "WRONG!"
                            feedbackColor = if (countryCode == answerKey) Color.Green else Color.Red
                            canGuess = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "Flag of $countryCode",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }

        Text(text = feedback, color = feedbackColor)

        if (!canGuess) {
            Button(
                onClick = {
                    flagOptions = countries.keys.shuffled().take(3)
                    answerKey = flagOptions.random()
                    feedback = ""
                    feedbackColor = Color.Black // Reset the feedback color
                    canGuess = true
                    timeLeft = 10 // Reset the timer
                    startTimer() // Restart the timer
                },
                modifier = Modifier.padding(top = 35.dp)
            ) {
                Text("Next")
            }
        }
    }
}

fun loadCountriesJson(context: Context): Map<String, String> = context.assets.open("countries.json").use { inputStream ->
    JSONObject(inputStream.bufferedReader().use { it.readText() }).let { jsonObject ->
        mutableMapOf<String, String>().apply {
            jsonObject.keys().forEach { key ->
                this[key] = jsonObject.getString(key)
            }
        }
    }
}


