package com.carvalho.cronometroservice.demo.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.carvalho.cronometroservice.databinding.ItemStopwatchBinding
import com.carvalho.cronometroservice.demo.model.Stopwatch
import com.carvalho.wrapper.CountdownClient

class StopwatchAdapter(private val context: Context) : RecyclerView.Adapter<StopwatchViewHolder>() {
    private var items = listOf<Stopwatch>()

    fun fetchItems(newItems: List<Stopwatch>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val view = ItemStopwatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StopwatchViewHolder(view, context)
    }

    override fun getItemCount(): Int = items.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class StopwatchViewHolder(private val binding: ItemStopwatchBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    private val countdownClient = CountdownClient(context)



    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(item: Stopwatch) {
        binding.tvStopwatch.text = "${item.name} - Timer: ${item.time}"

        binding.btnRun.setOnClickListener {
            Log.d("ItemHolder", "Tentando iniciar o serviço para $item")

            val context = binding.root.context

            try {
                countdownClient.bindService()
//                countdownClient.startCountdown(item.time)
                Log.d("ItemHolder", "Comando Enviando!")
            } catch (e: Exception) {
                Log.e("ItemHolder", "Erro ao iniciar contagem: ${e.message}")
                Toast.makeText(binding.root.context, "Erro ao iniciar contagem", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun onViewRecycled() {
        countdownClient.unbindService()
    }
}