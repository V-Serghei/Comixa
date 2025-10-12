package com.comixa.app.ui.labs.lab2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.adapter.lab2.SearchEventsAdapter
import com.comixa.app.databinding.ActivityLab2SearchBinding
import com.comixa.app.viewmodel.lab2.SearchEventsViewModel
import com.comixa.data.event.Event
import kotlinx.coroutines.flow.collectLatest

class SearchEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLab2SearchBinding
    private val vm: SearchEventsViewModel by viewModels()

    private lateinit var adapter: SearchEventsAdapter
    private var all: List<Event> = emptyList()
    private var selected: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab2SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = SearchEventsAdapter { selected = it }
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@SearchEventsActivity)
            adapter = this@SearchEventsActivity.adapter
            setHasFixedSize(true)
        }

        binding.etQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { applyFilter() }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnUpdate.setOnClickListener {
            val e = selected ?: return@setOnClickListener Toast.makeText(this, "Select an event", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, UpdateActivity::class.java).putExtra(UpdateActivity.EXTRA_EVENT_ID, e.id))
        }

        binding.btnRemove.setOnClickListener {
            val e = selected ?: return@setOnClickListener Toast.makeText(this, "Select the event to delete", Toast.LENGTH_SHORT).show()
            vm.delete(e.id) {
                runOnUiThread {
                    selected = null
                    applyFilter()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            vm.all.collectLatest { list ->
                all = list
                applyFilter()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.reload()
    }

    private fun applyFilter() {
        val q = binding.etQuery.text?.toString().orEmpty().trim()
        val filtered = if (q.isBlank()) emptyList()
        else all.filter { it.description.contains(q, ignoreCase = true) }.sortedBy { it.timeMillis }

        adapter.submit(filtered)
        binding.tvCount.text = if (q.isBlank()) "" else "${filtered.size} results"
        binding.emptyView.text = if (q.isBlank() || filtered.isNotEmpty()) "" else "Nothing found"
        selected = null
    }
}
