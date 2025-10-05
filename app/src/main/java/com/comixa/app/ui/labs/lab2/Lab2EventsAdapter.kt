package com.comixa.app.ui.labs.lab2

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

class Lab2EventsAdapter(
    private val onSelected: (Event) -> Unit
) : RecyclerView.Adapter<Lab2EventsAdapter.VH>() {

    private val items = mutableListOf<Event>()
    private var selectedPos = RecyclerView.NO_POSITION
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun submit(list: List<Event>) {
        items.clear()
        items.addAll(list)
        selectedPos = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_lab2_event, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.time.text = timeFmt.format(Date(e.timeMillis))
        holder.info.text = e.description
        holder.root.isSelected = position == selectedPos
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
        val root: View = v.findViewById(R.id.root)
        val time: TextView = v.findViewById(R.id.tvTime)
        val info: TextView = v.findViewById(R.id.tvInfo)
    }

}
