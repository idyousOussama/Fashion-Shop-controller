package com.example.store_application_control.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application_control.ApplicationObjs.Card
import com.example.store_application_control.ApplicationObjs.Order
import com.example.store_application_control.R

class OrderAdapter(var orederList :ArrayList<Order> = ArrayList()) : RecyclerView.Adapter<OrderAdapter.orderViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): orderViewHolder {
val view  = LayoutInflater.from(p0.context).inflate(R.layout.order_custom,p0,false)
       return orderViewHolder(view)
    }

    override fun getItemCount(): Int {
return        orederList.size
    }

    override fun onBindViewHolder(p0: orderViewHolder, p1: Int) {
        val order = orederList.get(p1)
        p0.getOrder(order.oderBuyer!!.buyerName,order.oderBuyer!!.buyerEmail,order.oderBuyer!!.buyerPhone,order.ordersList!!)
    }
    class orderViewHolder(itemView: View) : ViewHolder(itemView){
val buyername = itemView.findViewById<TextView>(R.id.buyer_name)
val buyeremail = itemView.findViewById<TextView>(R.id.buyer_email)
val buyerphone = itemView.findViewById<TextView>(R.id.buyer_phone)
val cardRV = itemView.findViewById<RecyclerView>(R.id.order_item_list)
  var cardsList :ArrayList<Card> = ArrayList()
 fun getOrder(name :String,email :String,phone :String,cardsList :ArrayList<Card>){
     buyername.setText(name)
     buyeremail.setText(email)
     buyerphone.setText(phone)
 this.cardsList = cardsList
     var orderCardAdapter : OrderCardAdapter? = OrderCardAdapter(cardsList)
     cardRV.layoutManager = LinearLayoutManager(itemView.context)
     cardRV.setHasFixedSize(true)
     cardRV.adapter = orderCardAdapter

 }

    }
}