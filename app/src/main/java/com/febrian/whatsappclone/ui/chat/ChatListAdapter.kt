package com.febrian.whatsappclone.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.febrian.whatsappclone.data.Chat
import com.febrian.whatsappclone.databinding.ItemChatBinding


class ChatListAdapter(private val listChat : ArrayList<Chat>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding : ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat : Chat){
            binding.title.text = chat.chatId.toString()
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, ChatActivity::class.java)
                val bundle = Bundle()
                bundle.putString("chatID", chat.chatId)
                intent.putExtras(bundle)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {
        val view = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        holder.bind(listChat[position])
    }

    override fun getItemCount(): Int {
        return listChat.size
    }
}