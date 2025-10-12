package com.comixa.app.adapter.lab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.databinding.ItemArticleBinding
import com.comixa.data.rss.ArticleEntity

class ArticlesAdapter(
    private val onClick: (ArticleEntity) -> Unit
) : ListAdapter<ArticleEntity, ArticlesAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ArticleEntity>() {
        override fun areItemsTheSame(old: ArticleEntity, new: ArticleEntity) = old.id == new.id
        override fun areContentsTheSame(old: ArticleEntity, new: ArticleEntity) =
            old.title == new.title && old.link == new.link && old.publishedEpochSec == new.publishedEpochSec
    }

    inner class VH(val vb: ItemArticleBinding) : RecyclerView.ViewHolder(vb.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(vb)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.vb.tvTitle.text = item.title ?: "Untitled"
        holder.vb.tvLink.text = item.link
        holder.vb.root.setOnClickListener { onClick(item) }
    }
}