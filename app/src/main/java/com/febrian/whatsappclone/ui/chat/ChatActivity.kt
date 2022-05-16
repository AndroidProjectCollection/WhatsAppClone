package com.febrian.whatsappclone.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.febrian.whatsappclone.data.Message
import com.febrian.whatsappclone.databinding.ActivityChatBinding
import com.febrian.whatsappclone.ui.chat.media.MediaAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private val listMessage = ArrayList<Message>()
    private val listMedia = ArrayList<String>()
    private val listMediaId = ArrayList<String>()

    private lateinit var chatID: String

    private lateinit var mChatDb: DatabaseReference

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mediaAdapter: MediaAdapter

    var totalMediaUploaded = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatID = intent.extras?.getString("chatID") ?: ""

        mChatDb = FirebaseDatabase.getInstance().reference.child("chat").child(chatID)

        binding.send.setOnClickListener {
            sendMessage()
        }
        binding.addMedia.setOnClickListener {
            openGallery()
        }

        initializeMessage()
        initializeMedia()
        getChatMessages()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture(s)"))
    }

    private fun getChatMessages() {
        mChatDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if (dataSnapshot.exists()) {

                    val listMedia = ArrayList<String>()

                    var text = ""
                    var creatorID = ""
                    if (dataSnapshot.child("text").value != null) text =
                        dataSnapshot.child("text").value.toString()
                    if (dataSnapshot.child("creator").value != null) creatorID =
                        dataSnapshot.child("creator").value.toString()
                    if (dataSnapshot.child("media").childrenCount > 0) for (mediaSnapshot in dataSnapshot.child(
                        "media"
                    ).children) listMedia.add(mediaSnapshot.value.toString())

                    val mMessage = Message(dataSnapshot.key.toString(), creatorID, text, listMedia)
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

    private fun initializeMessage() {
        messageAdapter = MessageAdapter(listMessage)
        binding.rvMessageList.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(applicationContext)
            scrollToPosition(listMessage.size - 1)
            adapter = messageAdapter
        }
    }

    private fun initializeMedia() {
        binding.rvMediaList.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            mediaAdapter = MediaAdapter(listMedia)
            adapter = mediaAdapter
        }
    }

    private fun sendMessage() {

        val messageId = mChatDb.push().key
        val newMessageDb = mChatDb.child(messageId!!)

        val newMessageMap: MutableMap<String, Any> = HashMap()

        newMessageMap["creator"] = FirebaseAuth.getInstance().uid.toString()

        if (binding.message.text.toString().isNotEmpty()) {
            newMessageMap["text"] = binding.message.text.toString()
        }

        if (listMedia.isNotEmpty()) {
            for (mediaUri in listMedia) {
                val mediaId = newMessageDb.child("media").push().key ?: ""
                listMediaId.add(mediaId)
                val filePath =
                    FirebaseStorage.getInstance().reference.child("chat").child(chatID)
                        .child(messageId).child(mediaId)
                val uploadTask = filePath.putFile(Uri.parse(mediaUri))
                uploadTask.addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {
                        newMessageMap["/media/" + listMediaId[totalMediaUploaded] + "/"] = it.toString()

                        totalMediaUploaded++ 
                        if (totalMediaUploaded == listMedia.size)
                            updateDatabaseWithNewMessage(newMessageDb, newMessageMap) 
                    }
                }
            }
        } else {
            if (binding.message.text.toString().isNotEmpty())
                updateDatabaseWithNewMessage(newMessageDb, newMessageMap) 
        }
    }

    private fun updateDatabaseWithNewMessage(
        newMessageDb: DatabaseReference,
        newMessageMap: MutableMap<String, Any>
    ) {
        newMessageDb.updateChildren(newMessageMap) 
        binding.message.text = null 
        listMedia.clear()
        listMediaId.clear() 
        totalMediaUploaded = 0 
        mediaAdapter.notifyDataSetChanged()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                Log.d("Result", "RESULT OK")

                if (result.data?.clipData == null) {
                    Log.d("Result", "2")
                    listMedia.add(result.data?.data.toString())
                } else {
                    Log.d("Result", "3")
                    for (i in 0 until result.data?.clipData!!.itemCount) {
                        listMedia.add(result.data?.clipData!!.getItemAt(i).uri.toString())
                    }
                }
                mediaAdapter.notifyDataSetChanged()
            }
        }
}