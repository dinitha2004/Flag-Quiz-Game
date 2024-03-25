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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

class GuessTheCountry : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AsdTheme {
                gussingTheCountry(this)
            }
        }
    }
}

fun getDrawableImages(): List<Int> {
    val fieldList = R.drawable::class.java.fields
    return fieldList.mapNotNull { field ->
        if (field.type == Int::class.java) {
            field.getInt(null) // get the resource ID
        } else {
            null
        }
    }
}



fun loadCountriesFromJson(context: Context): List<String> {
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val countryNames = mutableListOf<String>()
    jsonObject.keys().forEach { key ->
        countryNames.add(jsonObject.getString(key))
    }
    return countryNames
}



@Composable
fun gussingTheCountry(context: Context) {
    val imageList = remember { getDrawableImages() }
    val countries = remember { loadCountriesFromJson(context) }

    var currentImageIndex by remember { mutableStateOf(0) }
    var isGuessSubmitted by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }

    // Derived state for current image and country, to simplify logic.
    val currentImageId = imageList[currentImageIndex]
    val currentCountry = countries[currentImageIndex]

    // Determine guess correctness only after submission.
    val isGuessCorrect = remember(isGuessSubmitted, selectedCountry) {
        isGuessSubmitted && selectedCountry == currentCountry
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween, // Space the content between the top and the bottom of the container
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = currentImageId),
            contentDescription = "Country Flag",
            modifier = Modifier.size(width = 300.dp, height = 150.dp).padding(bottom = 16.dp) // Increased size
        )

        // Center the country names in the middle of the page.
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Takes up all available space between the image and the button
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(countries) { country ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (!isGuessSubmitted) selectedCountry = country }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = country, fontSize = 18.sp)
                }
            }
        }

        // Feedback and button at the bottom of the screen.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            if (isGuessSubmitted) {
                Text(
                    text = if (isGuessCorrect) "CORRECT!" else "WRONG!",
                    color = if (isGuessCorrect) Color.Green else Color.Red,
                    fontSize = 18.sp
                )
                if (!isGuessCorrect) {
                    Text(text = "Correct Country: $currentCountry", color = Color.Blue, fontSize = 18.sp)
                }
            }

            Button(
                onClick = {
                    if (isGuessSubmitted) {
                        currentImageIndex = (currentImageIndex + 1) % imageList.size
                        isGuessSubmitted = false
                        selectedCountry = null
                    } else {
                        isGuessSubmitted = true
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = if (isGuessSubmitted) "Next" else "Submit")
            }
        }
    }
}
