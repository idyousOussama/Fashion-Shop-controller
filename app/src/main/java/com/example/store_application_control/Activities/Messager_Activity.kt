package com.example.store_application_control.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store_application.AppliactionObjs.User
import com.example.store_application_control.Adapters.MessageAdapter
import com.example.store_application_control.ApplicationObjs.Message
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityMessagerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Messager_Activity : AppCompatActivity() {
    lateinit var binding : ActivityMessagerBinding
    lateinit var receiver : User
    lateinit var support : User
    var messagesAdapter = MessageAdapter()
    val firebaseDB = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getConversationEdges()
getConversationMessages()
initMessageList()
        removingNewMessages()
        goBack()
 binding.sendMessageBtn.setOnClickListener {
var messageText = binding.messageInput.text.toString()
            if(!messageText.isEmpty()){
                binding.messageInput.setText("")
                initRooms(messageText)
            }
        }
    }
    private fun goBack() {
        binding.messangerGoBack.setOnClickListener {
            finish()
        }
    }
    private fun removingNewMessages() {
        var conversationMessageRef = firebaseDB.getReference("Rooms").child(support.userId+receiver.userId).child("NewMessages")
        conversationMessageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
if (p0.exists()){
    for(item in p0.children){
        var newMessage = item.getValue(Message::class.java)
        if(newMessage != null){
            conversationMessageRef.child(newMessage.messageId).removeValue()
        }
    }
}
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initMessageList() {
        messagesAdapter.setUserSender(support)
        binding.messageList.layoutManager = LinearLayoutManager(this)
        binding.messageList.setHasFixedSize(true)
        binding.messageList.adapter = messagesAdapter
    }

    private fun getConversationMessages() {
        val conversationMessageRef = firebaseDB.getReference("Rooms")
val messagesList:ArrayList<Message> = ArrayList()
        conversationMessageRef.child(support.userId+receiver.userId).child("Messages").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var message = item.getValue(Message::class.java)
                        if (message != null){
                            messagesList.add(message)
                        }
                    }
                  if (messagesList.isNotEmpty()){
                      messagesAdapter.setListMessage(messagesList)
                      binding.messageList.smoothScrollToPosition(messagesAdapter.itemCount - 1)
                  }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initRooms(messageText :String) {
        val roomsRef = firebaseDB.getReference("Rooms")
        val senderRoom = roomsRef.child(support.userId + receiver.userId)
        val receiverRoom = roomsRef.child(receiver.userId + support.userId)
        var messageId:String = roomsRef.push().key.toString()
        val message = Message(messageId,messageText,support.userId)
        receiverRoom.child("Messages").child(messageId).setValue(message).addOnCompleteListener {task->
            if(task.isSuccessful){
                receiverRoom.child("NewMessages").child(messageId).setValue(message)
                senderRoom.child("Messages").child(messageId).setValue(message).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        messagesAdapter.addMessage(message)
                        binding.messageList.smoothScrollToPosition(messagesAdapter.itemCount - 1)
                    }

                }
            }

        }


    }

    private fun getConversationEdges() {
        var conversationEdgesIntent = intent
        receiver = conversationEdgesIntent.getSerializableExtra("receiver") as User
        support =  conversationEdgesIntent.getSerializableExtra("support") as User
        if (receiver != null){
            binding.recieverName.setText(receiver.userAccount!!.accountName)
        Picasso.get().load(receiver.userAccount!!.accountProfile).into(binding.recieverImg)
        }
    }
}