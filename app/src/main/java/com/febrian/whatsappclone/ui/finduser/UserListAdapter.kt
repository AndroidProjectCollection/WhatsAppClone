package com.febrian.whatsappclone.ui.finduser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.febrian.whatsappclone.data.User
import com.febrian.whatsappclone.databinding.ItemUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class UserListAdapter(private val listUser : ArrayList<User>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding : ItemUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            binding.name.text = user.name
            binding.phone.text = user.phone

            itemView.setOnClickListener {
                val key = FirebaseDatabase.getInstance().reference.child("chat").push().key

                FirebaseDatabase.getInstance().reference.child("user")
                    .child(FirebaseAuth.getInstance().uid!!).child("chat").child(
                        key!!
                    ).setValue(true)
                FirebaseDatabase.getInstance().reference.child("user")
                    .child(user.uid).child("chat").child(
                        key
                    ).setValue(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int {
        return listUser.size
    }
}