package com.example.weatherapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel){

    var city by remember {
        mutableStateOf("")
    }

    val weatherResult = viewModel.weatherResult.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()
    val primaryColor = if (isDarkTheme) Color(0xFF80D8FF) else Color(0xFF29B6F6)
    val secondaryColor = if (isDarkTheme) Color(0xFFFFF59D) else Color(0xFFFFD700)
    val backgroundColor = if (isDarkTheme) Color(0x00333366) else Color(0xFF87CEFA)
    val textFieldColour = if (isDarkTheme) Color.White else Color.Black
    Surface(
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    label = {
                        Text(text = "Search Location", color = textFieldColour)
                    },
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = textFieldColour,
                        unfocusedBorderColor = textFieldColour,
                        cursorColor = textFieldColour
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.getData(city)
                        }
                    )

                )

                IconButton(onClick = {
                    viewModel.getData(city)
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Location",
                        tint = textFieldColour
                    )
                }
            }

            when (val result = weatherResult.value) {
                is NetworkResponse.Error -> {
                    Text(text = result.message, color = Color.Red)
                }

                NetworkResponse.Loading -> {
                    CircularProgressIndicator(color = primaryColor)
                }

                is NetworkResponse.Success -> {
                    WeatherDetails(
                        data = result.data,
                        secondaryColor,
                        backgroundColor,
                        textFieldColour
                    )
                }

                null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to WeatherApp!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textFieldColour
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enter a location to get the latest weather updates.",
                            fontSize = 16.sp,
                            color = textFieldColour,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(80.dp),
                            tint = secondaryColor
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun WeatherDetails(data : WeatherModel,
                   secondaryColor: Color, backgroundColor: Color,
                   textFieldColour: Color){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp),
                tint = secondaryColor
            )

            Text(text = data.location.name + "," + data.location.country, fontSize = 30.sp, color = textFieldColour)

        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.current.temp_c}° C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textFieldColour
        )

        AsyncImage(
            modifier = Modifier.size(130.dp),
            model = "https:${data.current.condition.icon}".replace("64x64","128x128"),
            contentDescription = "Condition icon"
        )

        Text(
            text = data.current.condition.text,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = textFieldColour
        )

        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = backgroundColor
        ){
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Humidity", data.current.humidity, textFieldColour)
                    WeatherKeyVal("Precipitation", "${data.current.precip_mm} mm", textFieldColour)
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Heat Index", "${data.current.heatindex_c}° C", textFieldColour)
                    WeatherKeyVal("Wind Speed", "${data.current.wind_kph} km/h", textFieldColour)
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Local Time", data.location.localtime.split(" ")[1], textFieldColour)
                    WeatherKeyVal("Local Date", data.location.localtime.split(" ")[0], textFieldColour)
                }
            }
        }
    }
}

@Composable
fun WeatherKeyVal(key: String, value: String, color: Color) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
    }
}

@Preview
@Composable
fun WeatherPagePreview() {
    WeatherPage(viewModel = WeatherViewModel())
}