package com.comixa.app.ui.labs.lab2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.databinding.ActivityLab2AddBinding
import com.comixa.app.viewmodel.lab2.AddViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AddActivity : AppCompatActivity() {

    companion object { const val EXTRA_DAY_START = "day_start" }

    private lateinit var binding: ActivityLab2AddBinding
    private val vm: AddViewModel by viewModels()

    private val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2AddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        vm.initFromDayStart(intent.getLongExtra(EXTRA_DAY_START, -1L).takeIf { it > 0 })

        updateDateTimeLabels()

        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d -> vm.setDate(y, m, d); updateDateTimeLabels() },
                vm.cal.get(java.util.Calendar.YEAR),
                vm.cal.get(java.util.Calendar.MONTH),
                vm.cal.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnPickTime.setOnClickListener {
            TimePickerDialog(this, { _, h, min -> vm.setTime(h, min); updateDateTimeLabels() },
                vm.cal.get(java.util.Calendar.HOUR_OF_DAY),
                vm.cal.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnSave.setOnClickListener {
            val info = binding.etInfo.text?.toString().orEmpty()
            vm.save(info) { finish() }
        }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun updateDateTimeLabels() {
        binding.tvDate.text = dateFmt.format(vm.cal.time)
        binding.tvTime.text = timeFmt.format(vm.cal.time)
    }
}
