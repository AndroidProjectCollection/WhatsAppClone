package com.febrian.whatsappclone.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.febrian.whatsappclone.data.Message
import com.febrian.whatsappclone.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private var listMessage = ArrayList<Message>()

    private lateinit var chatID: String

    private lateinit var mChatDb: DatabaseReference

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatID = intent.extras?.getString("chatID") ?: ""

        mChatDb = FirebaseDatabase.getInstance().reference.child("chat").child(chatID)

        binding.send.setOnClickListener {
            sendMessage()
        }

        initializeRecycleView()
        getChatMessages()
    }

    private fun getChatMessages() {
        mChatDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if (dataSnapshot.exists()) {
                    var text = ""
                    var creatorID = ""
                    if (dataSnapshot.child("text").value != null) text =
                        dataSnapshot.child("text").value.toString()
                    if (dataSnapshot.child("creator").value != null) creatorID =
                        dataSnapshot.child("creator").value.toString()
                    val mMessage = Message(dataSnapshot.key ?: "", creatorID, text)
                    listMessage.add(mMessage)
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initializeRecycleView() {
        messageAdapter = MessageAdapter(listMessage)
        binding.rv.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(applicationContext)
            scrollToPosition(listMessage.size - 1)
            adapter = messageAdapter
        }
    }

    private fun sendMessage() {
        if (binding.message.text.toString().isNotEmpty()) {
            val newMessageDb = mChatDb.push()
            val newMessageMap: MutableMap<String, String> = HashMap()
            newMessageMap["text"] = binding.message.text.toString()
            newMessageMap["creator"] = FirebaseAuth.getInstance().uid.toString()
            newMessageDb.updateChildren(newMessageMap as Map<String, Any>)
        }
        binding.message.text = null
    }
}