package com.example.store_application.Adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.net.toUri

import com.example.store_application_control.ApplicationObjs.Product
import com.example.store_application_control.Listeners.ProductItemListener
import com.example.store_application_control.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductAdapter () : RecyclerView.Adapter<ProductAdapter.ProductCustomViewHolder>() {
    var productsList :ArrayList<Product> = ArrayList()
var productItemListner: ProductItemListener?= null
    fun setProductList (proLis:ArrayList<Product> ){
        productsList = proLis
        notifyDataSetChanged()
    }
fun setProListner (listener : ProductItemListener){
    productItemListner = listener
}
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductCustomViewHolder {
    val v = LayoutInflater.from(p0.context).inflate(R.layout.main_product_custom,p0,false)
   return ProductCustomViewHolder(v)
    }


    override fun getItemCount(): Int {
        return productsList.size
   }

    override fun onBindViewHolder(p0: ProductCustomViewHolder, p1: Int) {
        var product : Product  = productsList[p1]
        p0.bind(product.productName,product.productPrice.toString() , product.productImage)
        p0.itemView.setOnLongClickListener {
            productItemListner?.onItemLongClicked(product,p0.itemView)

         true}
    }

    class ProductCustomViewHolder(itemView: View) : ViewHolder(itemView) {
        var item = itemView
        var productImg :ImageView? = itemView.findViewById(R.id.product_custom_img)
        var productName : TextView? =  itemView.findViewById(R.id.product_costom_name)
        var productPrice : TextView? = itemView.findViewById(R.id.product_costom_price)
        fun bind (proName: String, proPrice: String, proImage: String){
                    Picasso.get().load(proImage.toUri()).placeholder(R.drawable.place_holder_image).into(productImg)
                productName?.setText(proName)
                productPrice?.setText(proPrice)
                productName?.setText(proName)
                productPrice?.setText(proPrice)

        }




    }
}
