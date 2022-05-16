package com.febrian.whatsappclone.ui.chat

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.febrian.whatsappclone.data.Message
import com.febrian.whatsappclone.databinding.ItemMessageBinding
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer

class MessageAdapter(private val listMessage: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val view = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.message.text = message.message
            binding.sender.text = message.senderId

            if (message.listMedia.isEmpty()
            ) binding.viewMedia.visibility = View.GONE

            binding.viewMedia.setOnClickListener {
                StfalconImageViewer.Builder(
                    itemView.context,
                    message.listMedia
                ) { view, image ->
                    Picasso.get().load(image).into(view)

                    /*  new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
                  .setStartPosition(0)
                  .show();*/

                }.show()
            }
        }
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        holder.bind(listMessage[position])
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }
}