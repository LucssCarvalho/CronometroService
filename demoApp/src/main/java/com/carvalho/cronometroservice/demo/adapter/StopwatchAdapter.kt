package com.carvalho.cronometroservice.demo.adapter

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.carvalho.cronometroservice.demo.model.Stopwatch
import com.carvalho.demoapp.databinding.ItemStopwatchBinding

class StopwatchAdapter : RecyclerView.Adapter<StopwatchViewHolder>() {
    private var items = listOf<Stopwatch>()

    fun fetchItems(newItems: List<Stopwatch>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val view = ItemStopwatchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StopwatchViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class StopwatchViewHolder(private val binding: ItemStopwatchBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(item: Stopwatch) {
        binding.tvStopwatch.text = "${item.name} - Timer: ${item.time}"

        binding.btnRun.setOnClickListener {
            Log.d("ItemHolder", "Tentando iniciar o serviço para $item")

            val context = binding.root.context

            val intent = Intent().apply {
                component = ComponentName(
                    "com.carvalho.stopwatchservice",
                    "com.carvalho.stopwatchservice.CountdownService"
                )
                putExtra("EXTRA_TIME", item.time)
            }

            try {
                context.startForegroundService(intent)
                Log.d("ItemHolder", "Serviço iniciado com sucesso!")
            } catch (e: Exception) {
                Log.e("ItemHolder", "Erro ao iniciar serviço: ${e.message}")
                Toast.makeText(context, "Erro ao iniciar serviço", Toast.LENGTH_SHORT).show()
            }
        }
    }
}