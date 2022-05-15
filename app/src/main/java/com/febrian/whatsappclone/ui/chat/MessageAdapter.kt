package com.febrian.whatsappclone.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.febrian.whatsappclone.data.Message
import com.febrian.whatsappclone.databinding.ItemMessageBinding

class MessageAdapter(private val listMessage : ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val view = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message){
            binding.message.text = message.message
            binding.sender.text = message.senderId

        }
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        holder.bind(listMessage[position])
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }
}