package com.comixa.app.ui.labs.lab2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.databinding.ActivityLab2AddBinding
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddActivity : AppCompatActivity() {

    companion object { const val EXTRA_DAY_START = "day_start" }

    private lateinit var binding: ActivityLab2AddBinding
    private lateinit var store: EventXmlStore
    private val cal: Calendar = Calendar.getInstance()

    private val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2AddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        store = EventXmlStore(this)

        intent.getLongExtra(EXTRA_DAY_START, -1L).takeIf { it > 0 }?.let {
            cal.timeInMillis = it
        }

        updateDateTimeLabels()

        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(y, m, d)
                    updateDateTimeLabels()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnPickTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, h, min ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    updateDateTimeLabels()
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnSave.setOnClickListener {
            val info = binding.etInfo.text?.toString().orEmpty()

            val event = Event(
                /* id = */ UUID.randomUUID().toString(),
                /* whenMillis = */ cal.timeInMillis,
                /* info = */ info
            )
            store.add(event)
            finish()
        }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun updateDateTimeLabels() {
        binding.tvDate.text = dateFmt.format(cal.time)
        binding.tvTime.text = timeFmt.format(cal.time)
    }
}
