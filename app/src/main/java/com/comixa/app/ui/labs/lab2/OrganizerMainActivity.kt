package com.comixa.app.ui.labs.lab2

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.databinding.ActivityLab2MainBinding
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore
import java.util.Calendar

class OrganizerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLab2MainBinding
    private lateinit var store: EventXmlStore
    private lateinit var adapter: Lab2EventsAdapter

    private var selectedDayStart: Long = dayStart(System.currentTimeMillis())
    private var selected: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        store = EventXmlStore(this)

        binding.calendar.setOnDateChangeListener { _: CalendarView, y: Int, m: Int, d: Int ->
            val cal = Calendar.getInstance()
            cal.set(y, m, d, 0, 0, 0)
            cal.set(Calendar.MILLISECOND, 0)
            selectedDayStart = cal.timeInMillis
            selected = null
            reload()
        }

        adapter = Lab2EventsAdapter {
            selected = it
        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@OrganizerMainActivity)
            adapter = this@OrganizerMainActivity.adapter
            setHasFixedSize(true)
        }

        binding.btnAdd.setOnClickListener {
            val i = Intent(this, AddActivity::class.java)
            i.putExtra(AddActivity.EXTRA_DAY_START, selectedDayStart)
            startActivity(i)
        }

        binding.btnUpdate.setOnClickListener {
            val e = selected
            if (e == null) {
                Toast.makeText(this, "Select an event from the list", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val i = Intent(this, UpdateActivity::class.java)
            i.putExtra(UpdateActivity.EXTRA_EVENT_ID, e.id)
            startActivity(i)
        }

        binding.btnRemove.setOnClickListener {
            val e = selected
            if (e == null) {
                Toast.makeText(this, "Select the event to delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            store.delete(e.id)
            selected = null
            reload()
        }
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchEventsActivity::class.java))
        }


        selectedDayStart = dayStart(System.currentTimeMillis())
        reload()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        val all = store.getAll()
        val start = selectedDayStart
        val end = start + 24L * 60 * 60 * 1000
        val dayEvents = all.filter { it.timeMillis in start until end }.sortedBy { it.timeMillis }
        adapter.submit(dayEvents)
        binding.emptyView.text = if (dayEvents.isEmpty()) "There are no events" else ""
    }

    private fun dayStart(millis: Long): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }
}
