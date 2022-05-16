package com.febrian.whatsappclone.ui.chat.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.febrian.whatsappclone.databinding.ItemMediaBinding

class MediaAdapter(private val listMedia : ArrayList<String>) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(media : String){
            Glide.with(itemView).load(media).into(binding.media)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMedia[position])
    }

    override fun getItemCount(): Int {
        return listMedia.size
    }
}