package com.example.asd

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.asd.ui.theme.AsdTheme
import org.json.JSONObject

class AdvancedLevel : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timerEnabled = intent.getBooleanExtra("timerEnabled", false) // Default to false if not found

        setContent {
            AsdTheme {
                // Assuming 'this' is a ComponentActivity or a subclass of it
                Surface(color = MaterialTheme.colorScheme.background) {
                    AdvancedLevelGame(loadCountriesFromJson3(this), this, timerEnabled)
                }
            }
        }

    }
}



fun loadCountriesFromJson3(context: Context): Map<String, String> {
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val countriesMap = mutableMapOf<String, String>()
    jsonObject.keys().forEach { key ->
        countriesMap[key] = jsonObject.getString(key)
    }
    return countriesMap
}


fun getRandomFlagsAndAnswers(countries: Map<String, String>, context: Context): Pair<List<Int>, List<String>> {
    val randomCountries = countries.keys.shuffled().take(3) // Get 3 random country codes
    val flags = mutableListOf<Int>()
    val answers = mutableListOf<String>()

    randomCountries.forEach { countryCode ->
        context.resources.getIdentifier(countryCode.toLowerCase(), "drawable", context.packageName).let {
            if (it != 0) { // Check if resource ID is found
                flags.add(it)
                answers.add(countries[countryCode]!!)
            }
        }
    }

    return Pair(flags, answers)
}


@Composable
fun AdvancedLevelGame(countries: Map<String, String>, context: Context, timerEnabled: Boolean) {
    var flagsAndAnswers by remember { mutableStateOf(getRandomFlagsAndAnswers(countries, context)) }
    var guesses by remember { mutableStateOf(mutableStateListOf("", "", "")) }
    var score by remember { mutableStateOf(0) }
    var guessCorrectness by remember { mutableStateOf(mutableStateListOf<Boolean?>(null, null, null)) }
    var attempts by remember { mutableStateOf(3) }
    var gameEnded by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current // Add this line to get the current LifecycleOwner

    // Existing variable declarations...
    var timeLeft by remember { mutableStateOf(0L) } // For displaying the timer countdown

    // Timer logic
    // Timer logic
    LaunchedEffect(timerEnabled, gameEnded) {
        if (timerEnabled && !gameEnded) {
            val timer = object : CountDownTimer(10000, 1000) { // 10-second timer with 1-second ticks
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = millisUntilFinished / 1000 // Update remaining time every second
                }

                override fun onFinish() {
                    if (attempts > 0) {
                        attempts--
                        gameEnded = attempts == 0
                        if (!gameEnded) {
                            this.start() // This restarts the timer from its initial duration (10 seconds)
                        }
                    }
                }
            }.start() // Start the timer immediately

            // Ensure the timer is cancelled appropriately to avoid leaks
        }
    }


    // Continue with the rest of your existing UI code...
    // Make sure to display the timer if enabled:
    if (timerEnabled) {
        Text(
            "Time left: $timeLeft seconds",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Blue
        )
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Score displayed in the top-right corner
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Score: $score",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        // Center content vertically
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            flagsAndAnswers.first.zip(flagsAndAnswers.second).forEachIndexed { index, (flagDrawable, correctAnswer) ->
                if (flagDrawable != 0) {
                    Image(
                        painter = painterResource(id = flagDrawable),
                        contentDescription = "Flag of $correctAnswer",
                        modifier = Modifier
                            .size(60.dp) // Specify a fixed size for the image
                    )
                    Text(correctAnswer) // Uncomment for testing to display the name of the flag
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .background(
                            color = when (guessCorrectness.getOrElse(index) { null }) {
                                true -> Color.Green // Correct answer
                                false -> Color.Red // Incorrect answer
                                else -> Color.White // Default for unattempted
                            }
                        )
                        .padding(2.dp)
                ) {
                    TextField(
                        value = guesses.getOrElse(index) { "" },
                        onValueChange = { newValue ->
                            guesses[index] = newValue
                            // Check the guess immediately after input
                            val isCorrect = newValue.equals(flagsAndAnswers.second[index], ignoreCase = true)
                            // Update guess correctness only if not already marked as correct
                            if (guessCorrectness.getOrElse(index) { false } != true) {
                                guessCorrectness[index] = isCorrect
                                if (isCorrect) {
                                    // Increment score immediately if the guess is correct
                                    score++
                                }
                            }
                        },
                        label = { Text("Enter Country Name") },
                        enabled = !gameEnded && (guessCorrectness.getOrElse(index) { false } != true), // Disable if game ended or guess is correct
                        textStyle = TextStyle(
                            color = when (guessCorrectness.getOrElse(index) { null }) {
                                true -> Color.Black // Use white for better contrast on green or red background.
                                false -> Color.Black // Keep consistent styling for immediate feedback.
                                else -> Color.Black // Default color for unattempted.
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            OutlinedButton(
                onClick = {
                    if (!gameEnded) {
                        if (attempts > 0) {
                            attempts--
                            guesses.forEachIndexed { index, guess ->
                                if (guessCorrectness[index] != true) {
                                    val isCorrect = guess.equals(flagsAndAnswers.second[index], ignoreCase = true)
                                    guessCorrectness[index] = isCorrect
                                    if (isCorrect) {
                                        score++
                                    }
                                }
                            }

                            gameEnded = guessCorrectness.all { it == true } || attempts == 0
                        }
                    } else {
                        flagsAndAnswers = getRandomFlagsAndAnswers(countries, context)
                        guesses = mutableStateListOf("", "", "")
                        guessCorrectness = mutableStateListOf(null, null, null)
                        score = 0
                        attempts = 3
                        gameEnded = false
                    }
                },
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                colors = outlinedButtonColors() // Customize the colors as needed
            ) {
                Text(if (gameEnded) "Next" else "Submit")
            }
        }

        // Display game result and score details at the bottom
        if (gameEnded) {
            DisplayGameEndDetails(guessCorrectness, flagsAndAnswers, guesses)
        }
        Text("Attempts Left: $attempts", modifier = Modifier.padding(top = 8.dp), Color.Magenta)
    }
}
@Composable
fun DisplayGameEndDetails(
    guessCorrectness: List<Boolean?>,
    flagsAndAnswers: Pair<List<Int>, List<String>>,
    guesses: List<String>
) {
    Surface(
        modifier = Modifier.padding(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (guessCorrectness.all { it == true }) {
                Text(
                    "Correct!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    "Wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "The names of the countries you got wrong:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                flagsAndAnswers.second.forEachIndexed { index, correctAnswer ->
                    if (guessCorrectness.getOrElse(index) { false } == false && guesses[index] != "") {
                        Text(
                            text = "• $correctAnswer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}