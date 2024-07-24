package com.thiennh23.realtimeweather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiennh23.realtimeweather.api.*
import com.thiennh23.realtimeweather.api.Constant.APIkey
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel(){

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(APIkey, city)
                if (response.isSuccessful) {
                    Log.i("Response Success", response.body().toString())

                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }

                } else {
                    Log.i("Response Error", response.message())
                    _weatherResult.value = NetworkResponse.Error("Fail to load data")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Fail to load data")
            }
        }
    }

}