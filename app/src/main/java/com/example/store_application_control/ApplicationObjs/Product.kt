package com.example.store_application_control.ApplicationObjs

import java.io.Serializable

class Product ( var productId :String ,var productName : String , var  productImage : String , var productContity : Int ,  var productDescription :String  , var  productCategory : String ,  var productPrice : Int , var productDelService :Int ,var  likesNum : Int) : Serializable {
    constructor() : this("","", "", 0,"","",0,0,0)}
