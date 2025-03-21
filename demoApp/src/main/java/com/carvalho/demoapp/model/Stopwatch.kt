package com.carvalho.demoapp.model

data class Stopwatch(
    val id: Int,
    val name: String,
    val time: Int,
    val isRunning: Boolean
)