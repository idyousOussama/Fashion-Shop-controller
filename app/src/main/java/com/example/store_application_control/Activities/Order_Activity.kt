package com.example.store_application_control.Activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store_application_control.Adapters.OrderAdapter
import com.example.store_application_control.ApplicationObjs.Order
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Order_Activity : AppCompatActivity() {
    lateinit var binding : ActivityOrderBinding
    var firebaseDB = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrderBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
getOrders()
        getNewOrders()
        goBack()
    }
    private fun goBack() {
        binding.newOrderGoBack.setOnClickListener {
            finish()
        }
    }
    private fun getOrders() {
        val ordersRef = firebaseDB.getReference("Orders")
       var ordersList :ArrayList<Order> = ArrayList()
        ordersRef.addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (item in p0.children){
                        var order = item.getValue(Order::class.java)
                        if (order != null){
                           ordersList.add(order)
                        }
                    }
                    if(ordersList.isNotEmpty()){
                        binding.ordersProgress.visibility = View.GONE
                        binding.ordersList.visibility = View.VISIBLE
                        setOrderList(ordersList)
                    }else{
                        binding.ordersProgress.visibility = View.GONE
                        binding.ordersList.visibility = View.GONE
                        binding.ordesNoResutLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getNewOrders() {
        val newOrderRef = firebaseDB.getReference("newOrders")
        newOrderRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var order = item.getValue(Order::class.java)
                        if(order != null){
                            newOrderRef.child(order.orderId).removeValue()
                        }
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setOrderList(ordersList: ArrayList<Order>) {
        var orederAdapte = OrderAdapter(ordersList)
        binding.ordersList.layoutManager = LinearLayoutManager(this)
        binding.ordersList.setHasFixedSize(true)
        binding.ordersList.adapter = orederAdapte
    }
}