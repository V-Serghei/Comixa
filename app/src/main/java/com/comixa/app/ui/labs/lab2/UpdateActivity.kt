package com.comixa.app.ui.labs.lab2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.databinding.ActivityLab2UpdateBinding
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateActivity : AppCompatActivity() {

    companion object { const val EXTRA_EVENT_ID = "event_id" }

    private lateinit var binding: ActivityLab2UpdateBinding
    private lateinit var store: EventXmlStore
    private val cal: Calendar = Calendar.getInstance()
    private var model: Event? = null

    private val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2UpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        store = EventXmlStore(this)

        val id = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        val e = store.getAll().firstOrNull { it.id == id }
        model = e
        if (e != null) {
            cal.timeInMillis = e.timeMillis
            binding.etInfo.setText(e.description)
            updateDateTimeLabels()
        } else {
            finish()
            return
        }

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
            model?.let {
                it.timeMillis = cal.timeInMillis
                it.description = binding.etInfo.text?.toString().orEmpty()
                store.update(it)
            }
            finish()
        }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun updateDateTimeLabels() {
        binding.tvDate.text = dateFmt.format(cal.time)
        binding.tvTime.text = timeFmt.format(cal.time)
    }
}
