package com.example.store_application_control.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.store_application_control.Adapters.SearchItemsAdapter
import com.example.store_application_control.ApplicationObjs.Product
import com.example.store_application_control.Listeners.SearchItemListener
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivitySearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Search_Activity : AppCompatActivity() {
    lateinit var binding : ActivitySearchBinding
    var firebaseDB = FirebaseDatabase.getInstance()
    var startRefrech = false
    var itemProductAdapter = SearchItemsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSerchBar()
        initRefrech()
        setListener()
        initGoBackBtn()
goBack()
    }
    private fun goBack() {
        binding.searchGoBackBtn.setOnClickListener {
            finish()
        }
    }
    private fun initGoBackBtn() {
        binding.searchGoBackBtn.setOnClickListener {
            finish()
        }
    }
    private fun setListener() {
        itemProductAdapter.setItemListener(object :SearchItemListener{
            override fun onItemLongClicked(product: Product, itemView: View) {
                popUpManu(itemView,product)
            }

        })
    }
    private fun initRefrech() {
        binding.noResultText.setOnClickListener {
            var searchText = binding.searchInupt.text.toString()
            if(startRefrech && !searchText.isEmpty()){
                getProuduct(searchText)
            }else if(searchText.isEmpty()){
                Toast.makeText(baseContext, R.string.add_text, Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun initSerchBar() {
        binding.searchInupt.addTextChangedListener { TextWatcher ->
            var searchText = binding.searchInupt.text.toString()
            if(!searchText.isEmpty()){
                initProgress(1)
                getProuduct(searchText)
            }else{
                binding.searchProductList.visibility= View.GONE
                binding.noResutLayout.visibility= View.GONE
            }
        }
    }
    private fun initProgress(i: Int) {
        binding.searchProductList.visibility= View.VISIBLE
        if (i==1){
            binding.searchProductList.visibility = View.GONE
            binding.noResutLayout.visibility = View.GONE
            binding.searchProductProgress.visibility = View.VISIBLE
        }else if (i == 2){
            binding.noResutLayout.visibility = View.GONE
            binding.searchProductProgress.visibility = View.GONE
            binding.searchProductList.visibility = View.VISIBLE
        }else if (i == 0){
            binding.searchProductList.visibility = View.GONE
            binding.searchProductProgress.visibility = View.GONE
            binding.searchList.setImageResource(R.drawable.refrech)
            binding.noResultText.setBackgroundResource(R.drawable.add_carts_btn_backround)
            binding.noResultText.setText(R.string.refrech_text)
            binding.noResutLayout.visibility = View.VISIBLE
            startRefrech = true
        }
        else{
            binding.searchProductList.visibility = View.GONE
            binding.searchProductProgress.visibility = View.GONE
            binding.noResutLayout.visibility = View.VISIBLE
        }

    }
    private fun getProuduct(searchText: String) {
        val productRef = firebaseDB.getReference("Products")
        var productList:ArrayList<Product> = ArrayList()
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var product = item.getValue(Product::class.java)
                        if(product != null){
                            if (product.productName.contains(searchText)){
                                productList.add(product)
                            }
                        }
                    }
                    if (!productList.isEmpty()){
                        initProgress(2)
                        setList(productList)
                    }else{
                        initProgress(3)
                    }

                }else{
                    initProgress(3)                }
            }

            override fun onCancelled(p0: DatabaseError) {
                initProgress(0)
            }

        })


    }
    private fun setList(productList: ArrayList<Product>) {
        itemProductAdapter.setSearchList(productList)
        binding.searchProductList.layoutManager = GridLayoutManager(this,1)
        binding.searchProductList.setHasFixedSize(true)
        binding.searchProductList.adapter = itemProductAdapter


    }


    private fun popUpManu(itemView:View,obj:Any) {
        val popUp = PopupMenu(this,itemView)
        popUp.menu.add(Menu.NONE,0,0,"UpDate")
        popUp.menu.add(Menu.NONE,1,1,"Delete")
        popUp.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId){
                0 ->{
                    updateProduct(obj)
                              }
                1 ->{
                    alertDeleteDialog(obj)
                }
            }

            true
        }
        popUp.show()
    }

    private fun alertDeleteDialog(obj: Any) {
        val dialogView = layoutInflater.inflate(R.layout.delete_items_custom,null)
        val deleteBtn =dialogView.findViewById<TextView>(R.id.delete_dialog_deleteBtn)
        val cancelBtn =dialogView.findViewById<ImageButton>(R.id.delete_item_dismess_dialog)
        val deletingLayout = dialogView.findViewById<RelativeLayout>(R.id.deleting_Progress_layout)
        val dialog_layout = dialogView.findViewById<RelativeLayout>(R.id.deldialog_layout)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
        deleteBtn.setOnClickListener {
            dialog_layout.visibility = View.GONE
            deletingLayout.visibility = View.VISIBLE
            deleteProduct(obj,dialog)
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun deleteProduct(obj: Any  , dialog: Dialog) {
        if (obj is Product){
            val productRef = firebaseDB.getReference("Products")
            productRef.child(obj.productId).removeValue().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    dialog.dismiss()
                }
            }
        }

    }

    private fun updateProduct(product: Any) {
        val upDateProduct = product as Product
        var productIntent = Intent(baseContext,Add_Product::class.java)
        productIntent.putExtra("product",upDateProduct)
        startActivity(productIntent)
    }


}