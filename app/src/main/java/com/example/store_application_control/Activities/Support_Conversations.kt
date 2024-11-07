package com.example.store_application_control.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store_application.AppliactionObjs.User
import com.example.store_application_control.Adapters.ConversationAdapter
import com.example.store_application_control.ApplicationObjs.Conversation
import com.example.store_application_control.ApplicationObjs.Message
import com.example.store_application_control.Listeners.ConversationListener
import com.example.store_application_control.databinding.ActivitySupportConversationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Support_Conversations : AppCompatActivity() {
   lateinit var binding : ActivitySupportConversationsBinding
   val firebaseDB = FirebaseDatabase.getInstance()
    var lastLMessage: Message? = null
    lateinit var support : User
    var conversationAdapter = ConversationAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   binding = ActivitySupportConversationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
getSupport()
        getConversationUser()
initConversationList()
        initMessageList()
        setConversationListner()
        goBack()
    }
    private fun goBack() {
        binding.newConverGoBack.setOnClickListener {
            finish()
        }
    }
    private fun getSupport() {
        var supportIntent = intent
support = supportIntent.getSerializableExtra("support") as User
    binding.supportConList
    }

    private fun setConversationListner() {
        conversationAdapter.setListner(object : ConversationListener{
            override fun onConversationListEmpty() {
                binding.converProgress.visibility = View.GONE
                binding.supportConList.visibility = View.VISIBLE

            }

            override fun onConversationItemClicked(receiver: User) {

                if(receiver != null && support != null){
                    var intent = Intent(baseContext,Messager_Activity::class.java)
                    intent.putExtra("receiver",receiver)
                    intent.putExtra("support",support)
                    startActivity(intent)
                }

            }

        })
    }

    private fun initMessageList() {
        binding.supportConList.layoutManager = LinearLayoutManager(this)
        binding.supportConList.setHasFixedSize(true)
        binding.supportConList.adapter = conversationAdapter
    }

    private fun initConversationList() {
     binding.supportConList.layoutManager = LinearLayoutManager(this)
     binding.supportConList.setHasFixedSize(true)
     binding.supportConList.adapter = conversationAdapter
    }

    private fun getConversationUser() {
        val supportConversations = firebaseDB.getReference("SupportConversations")
        val conversationsList:ArrayList<User> = ArrayList()
        // here I get All of userConversation
        supportConversations.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (items in p0.children){
                        var conversationUser = items.getValue(User::class.java)
                        if (conversationUser != null){
                            conversationsList.add(conversationUser)
                            Toast.makeText(baseContext,conversationUser!!.userId,Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (!conversationsList.isEmpty()){
                            getConversation(conversationsList)
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    // here I want init Coonversation (counter , user, lastMessage)
    private fun getConversation(conversationsList: ArrayList<User>) {
        var conversationLastMessage:Message? = null
        for (items in conversationsList){
            Toast.makeText(baseContext,items.userId,Toast.LENGTH_SHORT).show()
            var roomsRef = firebaseDB.getReference("Rooms")
            var supportRoom = roomsRef.child(support.userId+ items.userId)
            supportRoom.child("Messages").limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (item in p0.children) {
                        // Retrieve the last message from the item snapshot
                        conversationLastMessage = item.getValue(Message::class.java)
                        if (conversationLastMessage != null) {
getNewMessageCounter(items,conversationLastMessage!!,supportRoom)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    // Handle the error here
                    Toast.makeText(baseContext, "Failed to retrieve data: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getNewMessageCounter(items: User, conversationLastMessage: Message , supportRoom :DatabaseReference) {
var newMessageCounter:Int = 0
        supportRoom.child("NewMessage").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (item in p0.children){
                        var newMessage = item.getValue(Message::class.java)
if (newMessage != null){
    newMessageCounter++
}

   }
  conversationAdapter.addConversation(Conversation(items,conversationLastMessage.messageText,newMessageCounter))
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}