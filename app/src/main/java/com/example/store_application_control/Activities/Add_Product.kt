package com.example.store_application_control.Activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.store_application_control.Adapters.DialogCategoriesAdapter
import com.example.store_application_control.ApplicationObjs.Category
import com.example.store_application_control.Listeners.CategoryListener
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityAddProductBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.core.widget.addTextChangedListener
import com.example.store_application_control.ApplicationObjs.Product


class Add_Product : AppCompatActivity() {
    lateinit var binding : ActivityAddProductBinding
    private    lateinit var getProductLaunche : ActivityResultLauncher<Intent>
    private    lateinit var productImageUri : Uri
    private    var proImageIsReady = false
    private var proNameIsReady = false
    private var proPriceIsReady = false
    private var proDelServiceIsReady = false
    private    var proQuantityIsReady = false
    private var proDescriptionIsReady = false
    private lateinit var categoryAdater : DialogCategoriesAdapter
    private    lateinit var productCategoryName :String
    private    lateinit var proNameText:String
    private lateinit var proTextPrice : String
    private  lateinit var descriptionProText : String
    private  lateinit var quantityProText : String
    private  lateinit var delSerText : String
    private  var firebaseDB = FirebaseDatabase.getInstance()
    private  lateinit var dialog :Dialog
    var saveProductBtn_Layout:RelativeLayout?  = null
    var saveProductBtn:TextView?  = null
    var upDateProduct : Product?= null
    var saveProductBtn_Progess:ProgressBar?  = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getPermission()
        setSelectedImage()
        getInputes()
        nextBtnClicked()
    getUpDatedProduct()
        goBack()
    }
    private fun goBack() {
        binding.newProductGoBack.setOnClickListener {
            finish()
        }
    }
    private fun getUpDatedProduct() {
        val productIntent = intent
    upDateProduct = productIntent.getSerializableExtra("product") as? Product
        if(upDateProduct != null){
            binding.newProductImage.visibility = View.GONE
        binding.productNameInput.setText(upDateProduct!!.productName)
        binding.productDelServiceInput.setText(upDateProduct!!.productDelService.toString())
        binding.productContityInput.setText(upDateProduct!!.productContity.toString())
        binding.productDescription.setText(upDateProduct!!.productDescription)


        }
    }
    private fun nextBtnClicked() {
        binding.nextBtn.setOnClickListener {
            if(proImageIsReady){
                alertCategoryDialog()
            }else{
                Toast.makeText(baseContext,R.string.add_pro_image, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun alertCategoryDialog() {
        var view = layoutInflater.inflate(R.layout.select_category_cutom_dialog,null)
        saveProductBtn = view.findViewById<TextView>(R.id.dialogadd_newCategory)
        saveProductBtn_Layout = view.findViewById<RelativeLayout>(R.id.dialogadd_newCategory_layout)
        saveProductBtn_Progess = view.findViewById<ProgressBar>(R.id.addnewProduct_progress)
        var no_categories_layout = view.findViewById<RelativeLayout>(R.id.no_categories_layout)
        var add_categories_btn = view.findViewById<Button>(R.id.no_categories_btn)
        dialog= Dialog(this)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        getCategories(view!!.findViewById<RecyclerView>(R.id.category_dialog_List),no_categories_layout,saveProductBtn!!)
        saveProductBtn!!.setOnClickListener {
            initAddProductProgress(1)

            if (upDateProduct == null){
                upleaodProImage()
            }else{
                updateProduct()
            }

        }
        add_categories_btn.setOnClickListener {
            var intent = Intent(baseContext,Add_Category::class.java)
            startActivity(intent)
        }
        categoryAdater = DialogCategoriesAdapter(object: CategoryListener {
            override fun onCategoyNameClecked(categoryName: String?) {
                productCategoryName = categoryName!!
                saveProductBtn!!.isEnabled = true
                saveProductBtn_Layout!!.setBackgroundResource(R.drawable.brow_btn_backround)
            }
        })
    }
    private fun updateProduct() {
        if(productCategoryName.isNotEmpty()){
            var productRef = firebaseDB.getReference("Products")
            var proUpDates = mapOf<String,String>(
                "productName" to binding.productNameInput.text.toString(),
                "productContity" to binding.productContityInput.text.toString(),
                "productDescription" to binding.productDescription.text.toString(),
                "productCategory" to productCategoryName,
                "productPrice" to binding.productPriceInput.text.toString(),
                "productDelService" to binding.productDelServiceInput.text.toString())
            productRef.child(upDateProduct!!.productId).updateChildren(proUpDates).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    dialog.dismiss()
                    Toast.makeText(baseContext,R.string.product_upDatedText,Toast.LENGTH_SHORT).show()
               val intent = Intent(baseContext,MainActivity::class.java)
                    startActivity(intent)
                }

            }
        }else{
            Toast.makeText(baseContext,R.string.add_proCategory,Toast.LENGTH_SHORT).show()
        }
    }
    private fun initAddProductProgress(i: Int) {
        if (i == 1)
        {
            saveProductBtn_Layout!!.visibility = View.GONE
            saveProductBtn_Progess!!.visibility = View.VISIBLE

        }else{
            saveProductBtn_Layout!!.visibility = View.VISIBLE
            saveProductBtn_Progess!!.visibility = View.GONE
        }
    }
    private fun upleaodProImage() {
        var firebaseStorage = FirebaseStorage.getInstance()
        var formate = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        var date = Date()
        val filename: String = formate.format(date)
        var categoriesImgRef = firebaseStorage.getReference("ProductsImages").child(filename + ".store")
        categoriesImgRef.putFile(productImageUri).addOnSuccessListener(object :
            OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                categoriesImgRef.downloadUrl.addOnSuccessListener { uri ->
                    saveProduct(uri)
                }
            }
        })
    }
    private fun saveProduct(uri: Uri?) {
        var productRef = firebaseDB.getReference("Products")
        var productrtId = productRef.push().key
        var product = Product(productrtId.toString(),proNameText,uri.toString(),quantityProText.toInt(),descriptionProText,productCategoryName,proTextPrice.toInt(),delSerText.toInt(),0)
        productRef.child(productrtId!!).setValue(product).addOnCompleteListener { task->
            if (task.isSuccessful){
              Toast.makeText(baseContext ,"Product Added",Toast.LENGTH_SHORT).show()
                val intent = Intent(baseContext,MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun getCategories(recyclerView: RecyclerView?, noCatLayout: RelativeLayout, add_product: TextView) {
        var categoryRef =firebaseDB.getReference("Categories")
        var categoriesList:ArrayList<Category> = ArrayList<Category>()
        categoryRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    for (category  in p0.children){
                        var categories = category.getValue(Category::class.java)
                        Toast.makeText(baseContext, categories!!.categoryName , Toast.LENGTH_SHORT).show()
                        if (categories != null) {
                            categoriesList.add(categories)
                        }

                    }
                    if(categoriesList.size >= 1){
                        noCatLayout.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        add_product?.visibility = View.VISIBLE
                        setCategoriesList(recyclerView!!)
                        categoryAdater .setCategoryList(categoriesList)
                    }
                }else{
                    noCatLayout.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    add_product?.visibility = View.GONE
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,R.string.getCategory_canceled, Toast.LENGTH_SHORT).show()
            }
        })

    }
    private fun setCategoriesList(dialogRcyclerView: RecyclerView) {
        dialogRcyclerView.layoutManager = GridLayoutManager(baseContext,2)
        dialogRcyclerView.setHasFixedSize(true)
        dialogRcyclerView.adapter = categoryAdater
    }
    private fun getInputes() {
        binding.productNameInput.addTextChangedListener { TextWatcher ->
            proNameText = binding.productNameInput.text.toString()
            if (proNameText.isEmpty()) {
                ifInputEmptyText(binding.productNameInput)
                proNameIsReady = false
            } else {
                proNameIsReady = true
                ifInputesIsFull(binding.productNameInput)
            }

        }
        binding.productPriceInput.addTextChangedListener { TextWatcher ->
            proTextPrice = binding.productPriceInput.text.toString()
            if(proTextPrice.isEmpty()){
                ifInputEmptyText(binding.productPriceInput)
                proPriceIsReady = false
            }else{
                proPriceIsReady = true
                ifInputesIsFull(binding.productPriceInput)

            }
        }
        binding.productDelServiceInput.addTextChangedListener { TextWatcher->
            delSerText = binding.productDelServiceInput.text.toString()
            if(delSerText.isEmpty()){
                ifInputEmptyText( binding.productDelServiceInput)
                proDelServiceIsReady = false
            }else{
                proDelServiceIsReady = true
                ifInputesIsFull(binding.productDelServiceInput)
            }
        }
        binding.productContityInput.addTextChangedListener { TextWatcher->
            quantityProText =    binding.productContityInput.text.toString()
            if (quantityProText .isEmpty()){

                ifInputEmptyText( binding.productContityInput)
                proQuantityIsReady= false
            }else{
                proQuantityIsReady = true
                ifInputesIsFull(binding.productContityInput)
            }
        }
        binding.productDescription.addTextChangedListener { TextWatcher->
            descriptionProText =    binding.productDescription.text.toString()
            if (descriptionProText.isEmpty()) {
                ifInputEmptyText( binding.productDescription)
                proDescriptionIsReady= false
            }else{
                proDescriptionIsReady = true
                ifInputesIsFull(binding.productDescription)
            }
        }
    }
    private fun ifInputesIsFull(productNameInput: EditText) {
        productNameInput.setBackgroundResource(R.drawable.white_input_text_backround_res)
        if (proNameIsReady && proPriceIsReady && proDelServiceIsReady && proDescriptionIsReady && proQuantityIsReady){
            binding.nextBtn.isEnabled = true
            binding.nextBtn.setBackgroundResource(R.drawable.brow_btn_backround)
        }
    }
    private fun ifInputEmptyText(productNameInput: EditText) {
        productNameInput.setBackgroundResource(R.drawable.inpute_wrong_backround)
        productNameInput.setHint(R.string.inputsEmpty)
        binding.nextBtn.isEnabled = false
        binding.nextBtn.setBackgroundResource(R.drawable.disable_btns_backroud)
    }
    private fun setSelectedImage() {
        getProductLaunche = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                var selectedImage : Uri?= result.data!!.data
                binding.newProductImage.setImageURI(selectedImage)
                productImageUri = selectedImage!!
                proImageIsReady = true
            }
        }
    }
    private fun getPermission() {
        binding.newProductImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            }else{
                var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getProductLaunche.launch(intent)
            }
        }
    }
}