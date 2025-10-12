package com.comixa.app.adapter.lab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.R
import com.comixa.data.event.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchEventsAdapter(
    private val onSelected: (Event) -> Unit
) : RecyclerView.Adapter<SearchEventsAdapter.VH>() {

    private val items = mutableListOf<Event>()
    private var selectedPos = RecyclerView.NO_POSITION
    private val dtFmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    fun submit(list: List<Event>) {
        items.clear()
        items.addAll(list)
        selectedPos = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    fun getSelected(): Event? = items.getOrNull(selectedPos)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lab2_search_event, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.title.text = e.description
        holder.subtitle.text = dtFmt.format(Date(e.timeMillis))
        holder.itemView.isSelected = position == selectedPos
        holder.itemView.setOnClickListener {
            val old = selectedPos
            selectedPos = holder.bindingAdapterPosition
            if (old != RecyclerView.NO_POSITION) notifyItemChanged(old)
            notifyItemChanged(selectedPos)
            onSelected(e)
        }
    }

    override fun getItemCount() = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTitle)
        val subtitle: TextView = v.findViewById(R.id.tvSubtitle)
    }
}