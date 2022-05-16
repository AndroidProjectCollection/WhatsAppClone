package com.febrian.whatsappclone

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.febrian.whatsappclone.data.Chat
import com.febrian.whatsappclone.databinding.ActivityMainBinding
import com.febrian.whatsappclone.ui.chat.ChatListAdapter
import com.febrian.whatsappclone.ui.finduser.FindUserActivity
import com.febrian.whatsappclone.ui.otp.OtpSendActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var chatList = ArrayList<Chat>()
    private lateinit var mChatListAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Fresco.initialize(this)

        binding.findUser.setOnClickListener {
            startActivity(Intent(applicationContext, FindUserActivity::class.java))
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, OtpSendActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        getPermissions()
        initializeRecyclerView()
        getUserChatList()
    }

    private fun initializeRecyclerView() {
        mChatListAdapter = ChatListAdapter(chatList)
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mChatListAdapter
        }
    }

    private fun getUserChatList() {
        val mUserChatDB = FirebaseDatabase.getInstance().reference.child("user").child(
            FirebaseAuth.getInstance().uid!!
        ).child("chat")

        mUserChatDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val mChat = Chat(childSnapshot.key.toString())
                        var exists = false
                        for (mChatIterator in chatList) {
                            if (mChatIterator.chatId == mChat.chatId) exists = true
                        }
                        if (exists) continue
                        chatList.add(mChat)
                        mChatListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS
                ), 1
            )
        }
    }
}