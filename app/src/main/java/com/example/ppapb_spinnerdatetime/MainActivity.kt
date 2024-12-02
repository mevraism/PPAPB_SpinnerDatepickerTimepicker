package com.example.ppapb_spinnerdatetime

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.ppapb_spinnerdatetime.databinding.ActivityMainBinding
import com.example.ppapb_spinnerdatetime.databinding.DialogExitBinding
import java.util.Calendar

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var destinationCities: Array<String>
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var dateDepatureVar: String? = null
    private var destinationCityVar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDestinationSpinner()
        setupDatePicker()
        setupTimePicker()
        setupShowCustomDialogButton()
    }

    private fun setupDestinationSpinner() {
        // Tambahkan item pertama "Tujuan" sebagai teks yang ditampilkan
        destinationCities = arrayOf("Tujuan") + resources.getStringArray(R.array.destination)
        val adapterDestinationCities = object : ArrayAdapter<String>(
            this@MainActivity,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            destinationCities
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent).apply {
                    (this as TextView).setTextColor(Color.parseColor("#B3B3B3"))
                }
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getDropDownView(position, convertView, parent).apply {
                    (this as TextView).setTextColor(Color.WHITE)
                }
            }
        }

        binding.spinnerDestination.apply {
            adapter = adapterDestinationCities
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    destinationCityVar = if (position == 0) null else destinationCities[position]
                    if (destinationCityVar != null){
                        Toast.makeText(this@MainActivity, destinationCityVar, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(this@MainActivity, "PILIH KOTA TUJUAN ANDA!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.dateDepature.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.BlueTimePickerTheme, // Menerapkan custom theme
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Batasi agar tidak bisa memilih tanggal sebelum hari ini (karena ga bisa beli tiket masa lalu)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun setupTimePicker() {
        binding.timeDepature.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                R.style.BlueTimePickerTheme, // Menerapkan custom theme
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun setupShowCustomDialogButton() {
        binding.btnShowCustomDialog.setOnClickListener {
            val username = binding.username.text.toString().trim()
            if (validateInputs(username, selectedHour, selectedMinute, dateDepatureVar, destinationCityVar)) {
                showExitDialog(username)
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateDepatureVar = "$dayOfMonth/${month + 1}/$year"
        binding.dateDepature.text = dateDepatureVar
        Toast.makeText(this@MainActivity, dateDepatureVar, Toast.LENGTH_LONG).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedHour = hourOfDay
        selectedMinute = minute
        updateTimeDisplay()
    }

    private fun updateTimeDisplay() {
        val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
        binding.timeDepature.text = timeString
        Toast.makeText(this@MainActivity, timeString, Toast.LENGTH_LONG).show()
    }

    private fun validateInputs(
        username: String?,
        hour: Int,
        minute: Int,
        dateDepature: String?,
        destinationCity: String?
    ): Boolean {
        return if (username.isNullOrEmpty() || hour == 0 && minute == 0 || dateDepature.isNullOrEmpty() || destinationCity == null) {
            Toast.makeText(this, "TOLONG ISI SEMUA DATA PEMESANAN!", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun showExitDialog(username: String) {
        val dialog = DialogExit()
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("date", dateDepatureVar)
        bundle.putString("time", String.format("%02d:%02d", selectedHour, selectedMinute))
        bundle.putString("city", destinationCityVar)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "exitDialog")
    }
}

class DialogExit : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogExitBinding.inflate(layoutInflater)
        val username = arguments?.getString("username")
        val date = arguments?.getString("date")
        val time = arguments?.getString("time")
        val city = arguments?.getString("city")

        return AlertDialog.Builder(requireActivity()).apply {
            setView(binding.root)
            binding.btnYes.setOnClickListener {
                val intent = Intent(requireActivity(), InfoActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("date", date)
                intent.putExtra("time", time)
                intent.putExtra("city", city)
                startActivity(intent)
                dismiss()
            }
            binding.btnNo.setOnClickListener { dismiss() }
        }.create()
    }
}