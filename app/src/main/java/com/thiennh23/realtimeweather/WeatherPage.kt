package com.thiennh23.realtimeweather

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thiennh23.realtimeweather.api.NetworkResponse
import com.thiennh23.realtimeweather.api.WeatherModel

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {

    var city by remember {
        mutableStateOf("")
    }

    val weatherResult = viewModel.weatherResult.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 25.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = {
                    city = it
                },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        viewModel.getData(city)
                             },
                ),
                label = {
                    Text(text = "Search for any location")
                }
            )
            
            IconButton(onClick = {
                keyboardController?.hide()
                viewModel.getData(city)
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search button")
            }
        }


        when(val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                WeatherDetail(data = result.data)
            }
            null -> {}
        }
    }
}

@Composable
fun WeatherDetail(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                Modifier.size(40.dp)
            )
            
            Text(text = data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${data.current.temp_c}°C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https://${data.current.condition.icon}"
                .replace("64x64", "128x128"), //better image quality
            contentDescription = "Condition icon"
        )

        Text(
            text = data.current.condition.text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Card {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailValue("Humidity", data.current.humidity)
                    WeatherDetailValue("Wind speed", data.current.wind_kph+" km/h")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailValue("UV", data.current.uv)
                    WeatherDetailValue("Pressure", data.current.precip_mm+" mm")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailValue("Local date", data.location.localtime.split(" ")[1])
                    WeatherDetailValue("Local time", data.location.localtime.split(" ")[0])
                }
            }
        }
    }
}

@Composable
fun WeatherDetailValue(key: String, value: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }
}