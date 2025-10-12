package com.comixa.app.adapter.lab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.databinding.ItemSourceBinding
import com.comixa.data.rss.SourceEntity

class SourcesAdapter(
    private val onClick: (SourceEntity) -> Unit,
    private val onRefresh: (SourceEntity) -> Unit,
    private val onDelete: (SourceEntity) -> Unit
) : ListAdapter<SourceEntity, SourcesAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<SourceEntity>() {
        override fun areItemsTheSame(oldItem: SourceEntity, newItem: SourceEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SourceEntity, newItem: SourceEntity) =
            oldItem.url == newItem.url &&
                    oldItem.title == newItem.title &&
                    oldItem.lastUpdatedEpochSec == newItem.lastUpdatedEpochSec
    }

    inner class VH(val vb: ItemSourceBinding) : RecyclerView.ViewHolder(vb.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemSourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(vb)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.vb.tvTitle.text = item.title ?: item.url
        holder.vb.tvSubtitle.text = item.url

        holder.vb.root.setOnClickListener { onClick(item) }
        holder.vb.btnRefresh.setOnClickListener { onRefresh(item) }
        holder.vb.btnDelete.setOnClickListener { onDelete(item) }
    }
}