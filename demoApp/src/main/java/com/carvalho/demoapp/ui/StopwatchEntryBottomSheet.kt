package com.carvalho.demoapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carvalho.demoapp.model.Stopwatch
import com.carvalho.demoapp.databinding.FragmentStopwatchEntryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlin.random.Random

class StopwatchEntryBottomSheet : BottomSheetDialogFragment() {
    private val gson = Gson()
    private var _binding: FragmentStopwatchEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopwatchEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonSave.setOnClickListener {
            val timeText = binding.numberPicker.text.toString()
            val nameText = binding.nameStopwatch.text.toString().trim()

            val isTimeValid = validateTime(timeText)
            val isNameValid = validateName(nameText)

            if (isTimeValid && isNameValid) {
                saveStopwatch(nameText, timeText.toInt())
                clearFields()
            }
        }
    }

    private fun validateTime(timeText: String): Boolean {
        return when {
            timeText.isEmpty() -> {
                binding.numberPicker.error = "Informe o tempo"
                false
            }

            !timeText.all { it.isDigit() } -> {
                binding.numberPicker.error = "Digite apenas números"
                false
            }

            else -> {
                try {
                    val time = timeText.toInt()
                    if (time <= 0) {
                        binding.numberPicker.error = "O tempo deve ser maior que zero"
                        false
                    } else {
                        true
                    }
                } catch (e: NumberFormatException) {
                    binding.numberPicker.error = "Número muito grande"
                    false
                }
            }
        }
    }

    private fun validateName(nameText: String): Boolean {
        return when {
            nameText.isEmpty() -> {
                binding.nameStopwatch.error = "Informe um nome"
                false
            }

            nameText.length > 10 -> {
                binding.nameStopwatch.error = "Nome muito longo (máx. 10 caracteres)"
                false
            }

            !nameText.matches(Regex("^[a-zA-ZÀ-ÿ0-9 ]+\$")) -> {
                binding.nameStopwatch.error = "Nome contém caracteres inválidos"
                false
            }

            else -> true
        }
    }

    private fun clearFields() {
        binding.numberPicker.text.clear()
        binding.nameStopwatch.text.clear()
    }

    private fun saveStopwatch(stopwatchName: String, stopwatchTime: Int) {
        val context = requireContext().applicationContext
        val sharedPreferences = context.getSharedPreferences(
            "stopwatches_prefs", Context.MODE_PRIVATE
        )

        val stopwatchId = Random.nextInt(1000)
        val newStopwatch = Stopwatch(
            id = stopwatchId,
            name = stopwatchName,
            time = stopwatchTime,
            isRunning = false
        )
        Log.i("Fragment", "Save Stopwatch: $newStopwatch")

        val json = sharedPreferences.getString("stopwatches_list", "[]")
        val type = object : com.google.gson.reflect.TypeToken<List<Stopwatch>>() {}.type
        val stopwatchesList: MutableList<Stopwatch> = gson.fromJson(json, type) ?: mutableListOf()

        stopwatchesList.add(newStopwatch)

        val updatedJson = gson.toJson(stopwatchesList)
        sharedPreferences.edit().putString("stopwatches_list", updatedJson).apply()
        Log.d("Fragment", "Lista atualizada salva: $updatedJson")

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
