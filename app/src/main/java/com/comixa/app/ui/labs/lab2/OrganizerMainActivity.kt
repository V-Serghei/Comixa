package com.comixa.app.ui.labs.lab2

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.adapter.lab2.Lab2EventsAdapter
import com.comixa.app.databinding.ActivityLab2MainBinding
import com.comixa.data.event.Event
import kotlinx.coroutines.flow.collectLatest

class OrganizerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLab2MainBinding
    private val vm: OrganizerMainViewModel by viewModels()

    private lateinit var adapter: Lab2EventsAdapter
    private var selected: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.calendar.setOnDateChangeListener { _: CalendarView, y: Int, m: Int, d: Int ->
            selected = null
            vm.setDay(y, m, d)
        }

        adapter = Lab2EventsAdapter { selected = it }
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@OrganizerMainActivity)
            adapter = this@OrganizerMainActivity.adapter
            setHasFixedSize(true)
        }

        binding.btnAdd.setOnClickListener {
            startActivity(
                Intent(this, AddActivity::class.java)
                    .putExtra(AddActivity.EXTRA_DAY_START, vm.selectedDayStart.value)
            )
        }

        binding.btnUpdate.setOnClickListener {
            val e = selected ?: return@setOnClickListener Toast.makeText(this, "Select an event", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, UpdateActivity::class.java).putExtra(UpdateActivity.EXTRA_EVENT_ID, e.id)) // id = String
        }

        binding.btnRemove.setOnClickListener {
            val e = selected ?: return@setOnClickListener Toast.makeText(this, "Select the event to delete", Toast.LENGTH_SHORT).show()
            vm.delete(e.id) {
                runOnUiThread { selected = null }
            }
        }

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchEventsActivity::class.java))
        }

        lifecycleScope.launchWhenStarted {
            vm.events.collectLatest { list ->
                adapter.submit(list)
                binding.emptyView.text = if (list.isEmpty()) "There are no events" else ""
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.reload()
    }
}
