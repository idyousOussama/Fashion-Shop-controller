package com.example.store_application_control.Activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.store_application.Adapters.CategoriesAdapter
import com.example.store_application.Adapters.ProductAdapter
import com.example.store_application.Adapters.SliderBunnerAdaptetr
import com.example.store_application.AppliactionObjs.User
import com.example.store_application_control.ApplicationObjs.Bunner
import com.example.store_application_control.ApplicationObjs.Category
import com.example.store_application_control.ApplicationObjs.Order
import com.example.store_application_control.ApplicationObjs.Product
import com.example.store_application_control.Listeners.BunnerItemsListner
import com.example.store_application_control.Listeners.CategoryItemListener
import com.example.store_application_control.Listeners.ProductItemListener
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.Objects

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var productAdapter = ProductAdapter()
    var categoriyAdapter = CategoriesAdapter()
    var firebaseDB : FirebaseDatabase = FirebaseDatabase.getInstance()
   var siliderAdapter = SliderBunnerAdaptetr()
    var support: User?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBunners()
        getCategories()
        getProducts()
        allBtnClicked()
        getCategoryItemListener()
        getSupport()
        initNavigationBottom()
        productItemListener()
        setBunnerItemsListener()
        getNewOrders()
    }
    private fun getNewOrders() {
        var counter = 0
        val newOrderRef = firebaseDB.getReference("newOrders")
        newOrderRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                    for(item in p0.children){
                       var order = item.getValue(Order::class.java)
                        if(order != null){
                            counter++
                        }
                    }
                    if(counter > 0){
                        binding.orderCounter.visibility = View.VISIBLE
                        binding.orderCounter.setText(counter.toString())
                    }else{
                        binding.orderCounter.visibility = View.GONE

                    }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun setBunnerItemsListener() {
        siliderAdapter.setBunnerSliderListener(object : BunnerItemsListner{
            override fun onBuunerItemLongClicked(itemView: View, bunner: Bunner) {
                popUpManu(itemView,bunner)
            }

        })
    }
    private fun productItemListener() {
        productAdapter.setProListner (object: ProductItemListener{
            override fun onItemLongClicked(product: Product,itemView : View) {
                popUpManu(itemView,product)

            }

        })
    }
    private fun popUpManu(itemView:View,obj:Any) {
      val popUp = PopupMenu(this,itemView)
        popUp.menu.add(Menu.NONE,0,0,"UpDate")
        popUp.menu.add(Menu.NONE,1,1,"Delete")
        popUp.setOnMenuItemClickListener {menuItem ->
when(menuItem.itemId){
    0 ->{
 if (obj is Product){
     upDateProduct(obj)
 }else if (obj is Category){
val intent = Intent (baseContext,Add_Category::class.java)
    intent.putExtra("updatedCategory",obj)
     startActivity(intent)
     finish()
 }else if (obj is Bunner){
     val bunnerIntent = Intent (baseContext,UpDateBunner_Activity::class.java)
     bunnerIntent.putExtra("updatedBunner",obj)
     startActivity(bunnerIntent)
     finish()
 }
    }
    1 ->{
        if (obj is Product){
            deleteItemDialog(obj,"product")
        }else if (obj is Category){
            deleteItemDialog(obj,"category")
        }else if (obj is Bunner){
            deleteItemDialog(obj,"bunner")
        }
    }
}

            true
        }
        popUp.show()
    }
    private fun deleteItemDialog(obj: Any, itemType: String) {
 val dialogView = layoutInflater.inflate(R.layout.delete_items_custom,null)
val deleteItemBtn = dialogView.findViewById<TextView>(R.id.delete_dialog_deleteBtn)
val cncelBtn = dialogView.findViewById<ImageView>(R.id.delete_item_dismess_dialog)
        val deletingLayout = dialogView.findViewById<RelativeLayout>(R.id.deleting_Progress_layout)
        val dialog_layout = dialogView.findViewById<RelativeLayout>(R.id.deldialog_layout)
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        deleteItemBtn.setOnClickListener {
            dialog_layout.visibility = View.GONE
            deletingLayout.visibility = View.VISIBLE
            when(itemType){
"product" ->{
    deleteProduct(dialog,obj)
}
                "category" ->{
                    deleteCategory(dialog,obj)
                }
                "bunner" ->{
                    deletebunner(dialog,obj)
                }
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        cncelBtn.setOnClickListener {
            dialog.cancel()
        }

    }
    private fun deletebunner(dialog: Dialog, obj: Any) {
val bunnerRef = firebaseDB .getReference("Bunners")
        val bunner = obj as Bunner
        bunnerRef.child(bunner.id).removeValue().addOnCompleteListener {
            Toast.makeText(baseContext,"Bunner deleted Successful.",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
    private fun deleteCategory(dialog: Dialog, obj: Any) {
        val catrgoryRef = firebaseDB.getReference("Categories")
        val category :Category = obj as Category
        catrgoryRef.child(category.categoryId).removeValue().addOnCompleteListener { task ->
            Toast.makeText(baseContext , "category deleted successfull." , Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

    }
    private fun deleteProduct(dialog: Dialog, obj: Any) {
val productRef = firebaseDB.getReference("Products")
        val product: Product = obj as Product
        productRef.child(product.productId).removeValue().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(baseContext,"Product Removed" , Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }


        }
    }
    private fun upDateProduct(product: Product) {
var productIntent = Intent(baseContext,Add_Product::class.java)
        productIntent.putExtra("product" , product)
        startActivity(productIntent)
    }
    private fun initNavigationBottom() {
        binding.supportMessanger.setOnClickListener {
            var intent = Intent(baseContext , Support_Conversations::class.java)
            intent.putExtra("support", support)
            startActivity(intent)
        }
        binding.orderNotiLayout.setOnClickListener {
            getNewActivity(Order_Activity::class.java)
        }
        binding.addNewBunnerNav.setOnClickListener {
            getNewActivity(Add_Bunner_Activity::class.java)
        }
        binding.addNewProductNav.setOnClickListener {
            getNewActivity(Add_Product::class.java)
        }
        binding.addNewCategoryNav.setOnClickListener {
            getNewActivity(Add_Category::class.java)
        }
        binding.SearchNav.setOnClickListener {
            getNewActivity(Search_Activity::class.java)
        }
        binding.searchBarCard.setOnClickListener {
            getNewActivity(Search_Activity::class.java)
        }
        binding.homeNav.setOnClickListener {
            refrechProcess()
            getProducts()
            getBunners()
            getCategories()

        }
    }
    private fun refrechProcess() {
        binding.categoriesList.visibility = View.GONE
        binding.categoryProgress.visibility = View.VISIBLE
        binding.productList.visibility = View.GONE
        binding.productProgress.visibility = View.VISIBLE
        binding.bunnerContainer.visibility = View.GONE
        binding.bunnerProgress.visibility = View.VISIBLE
        binding.productCategorieName.setText(R.string.All_Text)    }
    private fun getNewActivity(newActivity: Class<*>) {
val intent = Intent(baseContext,newActivity)
        startActivity(intent)
    }
    private fun getSupport() {
        val supportIntent = intent
        support = supportIntent.getSerializableExtra("support") as? User
        Picasso.get().load(support?.userAccount?.accountProfile).placeholder(R.drawable.user_place_holder).into(binding.userPic)
        binding.userName.setText("Hello," + support!!.userName)
    }
    private fun getCategoryItemListener() {
        categoriyAdapter.setListener(object : CategoryItemListener {
            override fun onItemViewClicked(lastIteViewText: TextView, category: Category) {
                lastIteViewText.setTextColor(resources.getColor(R.color.black))
                binding.allText.setTextColor(resources.getColor(R.color.black))
                getCategoriesProduct(category.categoryName)
                binding.productCategorieName.setText(category.categoryName + " Products")
            }
            override fun onCtegoryItemLongClicked(itemView: View, category: Category) {
                popUpManu(itemView,category)
            }

        })    }
    private fun getCategoriesProduct(categoryName: String) {
        var catProRef = firebaseDB.getReference("Products")
        var catProList :ArrayList<Product> = ArrayList()
        catProRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var product = item.getValue(Product::class.java)
                        if (product != null) {
                            if (product.productName == categoryName){
                                catProList.add(product)
                            }
                        }
                    }
                    productAdapter.setProductList(catProList)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun allBtnClicked() {
        binding.allText.setOnClickListener {
            if(categoriyAdapter.lastItemViewTxet != null){
                categoriyAdapter.currentItem!!.setTextColor(resources.getColor(R.color.black))
                binding.allText.setTextColor(resources.getColor(R.color.brow))
                binding.productCategorieName.setText(R.string.All_Text)
                getProducts()
            }else{
            }
        }
    }
    private fun getProducts() {
        var productRef = firebaseDB.getReference("Products")
        var productList :ArrayList<Product> = ArrayList()
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var product = item.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }
                    if (productList.size > 0){
                        produtsExist(true)
                        setProducts(productList)
                    }else{
                        produtsExist(false)
                    }
                }else{
                    produtsExist(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun produtsExist(isExist: Boolean) {
        if(isExist){
            binding.productProgress.visibility = View.GONE
            binding.productList.visibility = View.VISIBLE
            binding.noProductSteker.visibility = View.GONE
        }else{
            binding.productProgress.visibility = View.GONE
            binding.productList.visibility = View.GONE
            binding.noProductSteker.visibility = View.VISIBLE
        }
    }
    private fun setProducts(productList: ArrayList<Product>) {
        productAdapter.productsList = productList
        binding.productList.layoutManager = GridLayoutManager(this,2,
            GridLayoutManager.VERTICAL,false)
        binding.productList.setHasFixedSize(true)

        binding.productList.adapter = productAdapter

    }
    private fun getCategories() {
        var categoriesRef = firebaseDB.getReference("Categories")
        var categoriesList :ArrayList<Category> = ArrayList()
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var category = item.getValue(Category::class.java)
                        if (category != null) {
                            categoriesList.add(category)
                        }
                    }
                    if(categoriesList.size > 0){
                        categoriesExists(true)
                        setCategories(categoriesList)
                    }
                }else{
                    categoriesExists(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun categoriesExists(cateExists: Boolean) {
        if (cateExists){
            binding.categoryProgress.visibility = View.GONE
            binding.noCategorySteker.visibility = View.GONE
            binding.categoriesList.visibility = View.VISIBLE
            binding.allText.visibility = View.VISIBLE
        }else{
            binding.categoryProgress.visibility = View.GONE
            binding.noCategorySteker.visibility = View.VISIBLE
            binding.categoriesList.visibility = View.GONE
            binding.allText.visibility = View.GONE
        }
    }
    private fun setCategories(categoriesList: ArrayList<Category>) {
        categoriyAdapter.categoryList = categoriesList
        binding.categoriesList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        binding.categoriesList.setHasFixedSize(true)
        binding.categoriesList.adapter = categoriyAdapter
    }
    private fun getBunners() {
        val bunnersRef = firebaseDB.getReference("Bunners")
        var bunnersList : ArrayList<Bunner> = ArrayList()
        bunnersRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    hideAndUnhideBunner(2)
                    for (item in p0.children){
                        var bunner = item.getValue(Bunner::class.java)
                        if (bunner != null) {
                            bunnersList.add(bunner)
                        }
                    }
                    if(bunnersList.size > 0){
                        binding.bunnerProgress.visibility = View.GONE
                        hideAndUnhideBunner(2)
                        initBunner(bunnersList)
                    }else{
                        hideAndUnhideBunner(1)
                    }
                }else{
                    hideAndUnhideBunner(1)
                }
            }



            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun hideAndUnhideBunner(i: Int) {
        if(i == 1){
            binding.bunnerContainer.visibility = View.GONE
            binding.bunnerDotsIndecator.visibility = View.GONE
        }else{
            binding.bunnerContainer.visibility = View.VISIBLE
            binding.bunnerDotsIndecator.visibility = View.VISIBLE
        }
    }
    private fun initBunner(bunnersList : ArrayList<Bunner>) {
        if(bunnersList.size == 1){
            binding
                .bunnerDotsIndecator.visibility = View.GONE
        }

        siliderAdapter.setBunner(bunnersList,binding.viewPager)
        binding.viewPager.adapter =siliderAdapter
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.clipToPadding =   false
        binding.viewPager.clipChildren = false
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val cpt = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPager.setPageTransformer(cpt)
        binding.bunnerDotsIndecator.attachTo(binding.viewPager)
    }

}