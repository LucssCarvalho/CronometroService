package com.carvalho.demoapp.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.carvalho.demoapp.model.Stopwatch
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StopwatchViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences(
        "stopwatches_prefs", Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private val _stopwatches = MutableStateFlow<List<Stopwatch>>(emptyList())
    val stopwatches: StateFlow<List<Stopwatch>> = _stopwatches

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "stopwatches_list") {
                fetchStopwatches()
            }
        }

    init {
        fetchStopwatches()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

//    fun updateRunningStopwatch(timeLeft: Int) {
//        val updatedList = _stopwatches.value.map { stopwatch ->
//            if (stopwatch.isRunning) {
//                stopwatch.copy(time = timeLeft)
//            } else {
//                stopwatch
//            }
//        }
//        _stopwatches.value = updatedList
//    }

    private fun fetchStopwatches() {
        val json = sharedPreferences.getString("stopwatches_list", "[]")
        val type = object : com.google.gson.reflect.TypeToken<List<Stopwatch>>() {}.type
        val stopwatchesList: List<Stopwatch> = gson.fromJson(json, type) ?: emptyList()
        _stopwatches.value = stopwatchesList
    }

    override fun onCleared() {
        super.onCleared()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
