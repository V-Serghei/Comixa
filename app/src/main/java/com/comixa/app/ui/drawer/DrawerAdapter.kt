package com.comixa.app.ui.drawer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.R
import com.comixa.app.infrastructure.system_numbers.DrawerConstants
import com.comixa.app.model.DrawerSection
import com.comixa.app.model.DrawerSubItem

class DrawerAdapter(
    private val navController: NavController,
    private val drawerLayout: DrawerLayout,
    private val sections: MutableList<DrawerSection>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val flat = mutableListOf<Any>()

    init { rebuild() }

    @SuppressLint("NotifyDataSetChanged")
    private fun rebuild() {
        flat.clear()
        sections.forEach { s ->
            flat.add(s)
            if (s.expanded) flat.addAll(s.subItems)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (flat[position] is DrawerSection) DrawerConstants.TYPE_SECTION else DrawerConstants.TYPE_SUB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return if (viewType == DrawerConstants.TYPE_SECTION) {
            val v = inf.inflate(R.layout.item_drawer_section, parent, false)
            SectionVH(v)
        } else {
            val v = inf.inflate(R.layout.item_drawer_subitem, parent, false)
            SubVH(v)
        }
    }

    override fun getItemCount(): Int = flat.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SectionVH -> {
                val s = flat[position] as DrawerSection
                holder.title.text = s.title
                holder.icon.setImageResource(s.iconRes)
                holder.chevron.rotation = if (s.expanded) 180f else 0f
                holder.itemView.setOnClickListener {
                    s.expanded = !s.expanded
                    rebuild()
                }
            }
            is SubVH -> {
                val sub = flat[position] as DrawerSubItem
                holder.title.text = sub.title
                holder.itemView.setOnClickListener {
                    if (navController.currentDestination?.id != sub.destinationId) {
                        navController.navigate(sub.destinationId)
                    }
                    drawerLayout.closeDrawers()
                }
            }
        }
    }

    private class SectionVH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val icon: ImageView = v.findViewById(R.id.icon)
        val chevron: ImageView = v.findViewById(R.id.chevron)
    }
    private class SubVH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
    }
}
