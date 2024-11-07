package com.example.store_application_control.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application_control.ApplicationObjs.Card
import com.example.store_application_control.R
import com.squareup.picasso.Picasso

class OrderCardAdapter(var orderCardList :ArrayList<Card>) : RecyclerView.Adapter<OrderCardAdapter.OrderCardViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderCardViewHolder {
val view = LayoutInflater.from(p0.context).inflate(R.layout.order_card_custom,p0,false)
        return OrderCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderCardList.size    }

    override fun onBindViewHolder(p0: OrderCardViewHolder, p1: Int) {
var card = orderCardList.get(p1)
        p0.getCard(card.cardProduct!!.productName,card.cardsNumber.toString(),(card.cardProduct!!.productPrice!!*card.cardsNumber!!).toString(),card.cardProduct!!.productImage.toUri())
    }
    class OrderCardViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardName = itemView.findViewById<TextView>(R.id.card_costom_name)
        val cardPrice = itemView.findViewById<TextView>(R.id.card_costom_price)
        val cardPiece = itemView.findViewById<TextView>(R.id.numberCard)
        val cardImage = itemView.findViewById<ImageView>(R.id.card_custom_img)
        fun getCard (name:String,price:String,numCard:String, image: Uri){
            cardName.setText(name)
            cardPrice.setText(price)
            cardPiece.setText(numCard)
            Picasso.get().load(image).placeholder(R.drawable.place_holder_image).into(cardImage)
        }

    }
}