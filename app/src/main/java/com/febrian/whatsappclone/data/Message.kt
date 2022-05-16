package com.febrian.whatsappclone.data

data class Message(
    var messageId : String,
    var senderId : String,
    var message : String,
    var listMedia : ArrayList<String>
)
