package com.comixa.app.ui.labs.lab3.items

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.databinding.ItemLab3ArticleBinding
import com.comixa.data.rss.ArticleEntity

class ItemsAdapter(
    private val onOpen: (String) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.VH>() {

    private val current = mutableListOf<ArticleEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun submit(list: List<ArticleEntity>) {
        current.clear()
        current.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH =
        VH(ItemLab3ArticleBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, i: Int) = h.bind(current[i])
    override fun getItemCount() = current.size

    inner class VH(private val b: ItemLab3ArticleBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: ArticleEntity) {
            b.tvTitle.text = item.title
            b.tvSummary.text = item.summary ?: item.link
            b.root.setOnClickListener { onOpen(item.link) }
        }
    }
}
