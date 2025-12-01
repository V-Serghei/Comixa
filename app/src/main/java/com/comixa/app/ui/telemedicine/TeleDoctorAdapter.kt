package com.comixa.app.ui.telemedicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.R

class TeleDoctorAdapter(
    private val items: List<TeleDoctorUi>,
    private val onClick: (TeleDoctorUi) -> Unit
) : RecyclerView.Adapter<TeleDoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvDoctorName)
        val tvSpec: TextView = itemView.findViewById(R.id.tvDoctorSpeciality)
        val tvRating: TextView = itemView.findViewById(R.id.tvDoctorRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tele_doctor, parent, false)
        return DoctorViewHolder(v)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvSpec.text = item.speciality
        holder.tvRating.text = item.rating

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
