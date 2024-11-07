package com.example.store_application_control.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application_control.Activities.Support_Conversations
import com.example.store_application_control.ApplicationObjs.Conversation
import com.example.store_application_control.ApplicationObjs.Message
import com.example.store_application_control.Listeners.ConversationListener
import com.example.store_application_control.R
import com.squareup.picasso.Picasso

class ConversationAdapter  : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>(){
    var conversationList :ArrayList<Conversation> = ArrayList()
var lestener :ConversationListener? = null

    fun addConversation(conversation: Conversation){
    conversationList.add(conversation)
    if (conversationList.size == 1){
        lestener!!.onConversationListEmpty()
    }
    notifyDataSetChanged()
}


fun setListner(listner :ConversationListener){
    this.lestener = listner
}
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ConversationViewHolder {
val view = LayoutInflater.from(p0.context).inflate(R.layout.conversation_item_custom,p0,false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    override fun onBindViewHolder(p0: ConversationViewHolder, p1: Int) {
val conversation = conversationList.get(p1)
        p0.getConversation(conversation.user!!.userAccount!!.accountProfile,conversation.user!!.userAccount!!.accountName,conversation.messageCounter,
            conversation.lastMessage.toString()
        )
        p0.itemView.setOnClickListener{
            lestener!!.onConversationItemClicked(conversation.user!!)
        }
    }
    class ConversationViewHolder(itemView: View) : ViewHolder(itemView) {
        val conversationImage = itemView.findViewById<ImageView>(R.id.cover_userImage)
        val conversationName = itemView.findViewById<TextView>(R.id.cover_userName)
        val conversationCounter = itemView.findViewById<TextView>(R.id.cover_counterMessage)
        val conversationLastMessage = itemView.findViewById<TextView>(R.id.cover_lastMessage)

        fun  getConversation(image : String,name :String , counter :Int,lastMessage: String){
            Picasso.get().load(image.toUri()).placeholder(R.drawable.place_holder_image).into(conversationImage)
            conversationName.setText(name)
            conversationLastMessage.setText(lastMessage)
            if (counter != 0){
                conversationCounter.visibility = View.VISIBLE
                conversationCounter.setText(counter.toString())
            }else{
                conversationCounter.visibility = View.GONE

            }

        }

    }

}