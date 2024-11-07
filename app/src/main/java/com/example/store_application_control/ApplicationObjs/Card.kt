package com.example.store_application_control.ApplicationObjs

import java.io.Serializable

class Card (var cardId : String ="" , var cardProduct: Product? = null ,var cardsNumber : Int = 1 ,var selectedToRemove:Boolean = false,var cardPsition : Int = 0) :Serializable {
    constructor() :this ("",null,1,false)
}