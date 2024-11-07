package com.example.store_application_control.ApplicationObjs

data class Order (var orderId : String ,var ordersList : ArrayList<Card>? ,var totalPrice:String,var oderBuyer  : Buyer?) {
    constructor() : this("", null,"",null)
}