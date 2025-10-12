package com.comixa.app.ui.labs.lab2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.databinding.ActivityLab2SearchBinding
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore

class SearchEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLab2SearchBinding
    private lateinit var store: EventXmlStore
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

        store = EventXmlStore(this)

        adapter = SearchEventsAdapter { selected = it }
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@SearchEventsActivity)
            adapter = this@SearchEventsActivity.adapter
            setHasFixedSize(true)
        }

        // Поиск при вводе
        binding.etQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { applyFilter() }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnUpdate.setOnClickListener {
            val e = selected
            if (e == null) {
                Toast.makeText(this, "Select an event from the list", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, UpdateActivity::class.java).putExtra(UpdateActivity.EXTRA_EVENT_ID, e.id))
        }

        binding.btnRemove.setOnClickListener {
            val e = selected
            if (e == null) {
                Toast.makeText(this, "Select the event to delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            store.delete(e.id)
            // Обновим данные после удаления
            reloadAll()
            applyFilter()
            selected = null
        }
    }

    override fun onResume() {
        super.onResume()
        reloadAll()
        applyFilter()
    }

    private fun reloadAll() {
        all = store.getAll()
    }

    private fun applyFilter() {
        val q = binding.etQuery.text?.toString().orEmpty().trim()
        val filtered = if (q.isBlank()) {
            emptyList()
        } else {
            all.filter { it.description.contains(q, ignoreCase = true) }
                .sortedBy { it.timeMillis }
        }
        adapter.submit(filtered)
        binding.tvCount.text = if (q.isBlank()) "" else "${filtered.size} results"
        binding.emptyView.text = if (q.isBlank() || filtered.isNotEmpty()) "" else "Nothing found"
        selected = null
    }
}
