package com.carvalho.cronometroservice.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carvalho.cronometroservice.demo.adapter.StopwatchAdapter
import com.carvalho.cronometroservice.databinding.ActivityMainBinding
import com.carvalho.cronometroservice.demo.ui.StopwatchEntryBottomSheet
import com.carvalho.cronometroservice.demo.ui.StopwatchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: StopwatchViewModel by viewModels()

    private lateinit var adapter: StopwatchAdapter

//    private val countdownReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val timeLeft = intent?.getIntExtra(CountdownService.EXTRA_TIME_LEFT, -1) ?: -1
//
//            if (timeLeft >= 0) {
//                viewModel.updateRunningStopwatch(timeLeft)
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StopwatchAdapter()
        binding.rvStopWatch.layoutManager = LinearLayoutManager(this)
        binding.rvStopWatch.adapter = adapter

        lifecycleScope.launch {
            viewModel.stopwatches.collectLatest { stopwatches ->
                adapter.fetchItems(stopwatches)
            }
        }

        binding.createStopwatch.setOnClickListener {
            val bottomSheet = StopwatchEntryBottomSheet()
            bottomSheet.show(supportFragmentManager, "StopwatchEntryBottomSheet")
        }
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
//        val filter = IntentFilter(CountdownService.COUNTDOWN_BROADCAST)
//        registerReceiver(countdownReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(countdownReceiver)
    }
}
