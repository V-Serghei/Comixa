package com.comixa.app.ui.labs.lab2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.comixa.app.databinding.ActivityLab2UpdateBinding
import com.comixa.app.viewmodel.lab2.UpdateViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateActivity : AppCompatActivity() {

    companion object { const val EXTRA_EVENT_ID = "event_id" }

    private lateinit var binding: ActivityLab2UpdateBinding
    private val vm: UpdateViewModel by viewModels()

    private val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2UpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ✅ Читаем как Long, без кастов
        val id = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        if (id <= 0L) { finish(); return }
        vm.load(id)

        lifecycleScope.launchWhenStarted {
            vm.model.collectLatest { e ->
                e ?: return@collectLatest
                binding.etInfo.setText(e.description)
                updateDateTimeLabels()
            }
        }

        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d -> vm.setDate(y, m, d); updateDateTimeLabels() },
                vm.cal.get(Calendar.YEAR),
                vm.cal.get(Calendar.MONTH),
                vm.cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnPickTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, h, min -> vm.setTime(h, min); updateDateTimeLabels() },
                vm.cal.get(Calendar.HOUR_OF_DAY),
                vm.cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnSave.setOnClickListener {
            vm.save(binding.etInfo.text?.toString().orEmpty()) { finish() }
        }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun updateDateTimeLabels() {
        binding.tvDate.text = dateFmt.format(vm.cal.time)
        binding.tvTime.text = timeFmt.format(vm.cal.time)
    }
}
