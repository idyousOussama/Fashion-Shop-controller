package com.example.store_application_control.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.store_application_control.ApplicationObjs.Category
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityAddCategoryBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener



class Add_Category : AppCompatActivity() {
    lateinit var binding : ActivityAddCategoryBinding
    var firebaseDB = FirebaseDatabase.getInstance()
    var categoriesRef  = firebaseDB.getReference("Categories")
    var CategoriesFireCloud = FirebaseStorage.getInstance()
    lateinit var  newCategoryImage : Uri
    var  categoryLunche : ActivityResultLauncher<Intent>?= null
    var categoryNameIsReady = false
    var categoryImageIsReady = false
    var upDateCategory : Category? =  null
    override fun onCreate(savedInstanceState: Bundle?) {
binding  = ActivityAddCategoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getCategoryImage()
        userProfileLancherfun()
        checCategoryInfo()
        saveCategoriesImage()
        getUpDateCategory()
        goBack()
    }
    private fun goBack() {
        binding.newCategoryGoBack.setOnClickListener {
            finish()
        }
    }
    private fun getUpDateCategory() {
        val categoryIntent = intent
upDateCategory = categoryIntent.getSerializableExtra("updatedCategory") as? Category
        if (upDateCategory != null){
            binding.newCatedgoryImage.visibility = View.GONE
            binding.categoryNameInput.setText(upDateCategory!!.categoryName)
        categoryImageIsReady = true
        }
    }

    private fun checCategoryInfo() {
        binding.categoryNameInput.addTextChangedListener { TextWatcher ->
            var categorynameText =binding.categoryNameInput.text.toString()
            if(categorynameText.isEmpty()){
                binding.categoryNameInput.setBackgroundResource(R.drawable.inpute_wrong_backround)
                binding.categoryNameInput.setHint(R.string.empty_categoryname_hint)
                categoryNameIsReady = false
                binding.addNewCategoryBtn.setBackgroundResource(R.drawable.disable_btns_backroud)
                binding.addNewCategoryBtn.isEnabled = false
            }else{
                binding.categoryNameInput.setBackgroundResource(R.drawable.white_input_text_backround_res)
                categoryNameIsReady = true
                binding.addNewCategoryBtn.setBackgroundResource(R.drawable.brow_btn_backround)
                binding.addNewCategoryBtn.isEnabled =true
            }

        }
    }

    private fun saveCategoriesImage() {
        binding.addNewCategoryBtn.setOnClickListener {
            if (upDateCategory == null){
                if (categoryImageIsReady){
                    progressAndSaveBtnOperation(0)
                    var formate = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                    var date = Date()
                    val filename: String = formate.format(date)
                    var categoriesImgRef = CategoriesFireCloud.getReference("CategoriesImages").child(filename + ".store")
                    categoriesImgRef.putFile(newCategoryImage).addOnSuccessListener(object :
                        OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                            // التعامل مع نجاح رفع الملف
                            Toast.makeText(baseContext,"File uploaded successfully!", Toast.LENGTH_SHORT).show()
                            // يمكنك هنا الحصول على رابط الصورة بعد رفعها
                            categoriesImgRef.downloadUrl.addOnSuccessListener { uri ->
                                addNewCategory(uri)

                            }
                        }
                    })

                        .addOnFailureListener { exception ->
                            // التعامل مع فشل رفع الملف
                            Toast.makeText(baseContext,"the dowLoad is failuer", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(baseContext,R.string.image_null_text, Toast.LENGTH_SHORT).show()
                }

            }else{
                UpDateCategory()
            }

        }


    }

    private fun UpDateCategory() {
        var categoryUpDates = mapOf<String ,String>(
            "categoryName" to binding.categoryNameInput.text.toString()
        )
        categoriesRef.child(upDateCategory!!.categoryId).updateChildren(categoryUpDates).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(baseContext,"category updated" ,Toast.LENGTH_SHORT).show()
                val intent = Intent (baseContext,MainActivity:: class.java)
                startActivity(intent)
            }
        }
    }

    private fun addNewCategory(categoryImageUri: Uri?) {
        var categoryId =categoriesRef.push().key!!
        var categoryName = binding.categoryNameInput.text.toString()
        var category = Category(categoryName,categoryImageUri.toString(), categoryId)
        categoriesRef.child(categoryId).setValue(category).addOnCompleteListener { task ->
            if (task.isSuccessful){
                addCategoeySetting(1)

            }
        }.addOnFailureListener {
            addCategoeySetting(2)
            progressAndSaveBtnOperation(0)
        }
    }

    private fun addCategoeySetting(i: Int) {
        if(i == 1){
            binding.categoryNameInput.setText("")
            binding.categoryNameInput.setBackgroundResource(R.drawable.white_input_text_backround_res
            )
            binding.addNewCategoryBtn.isEnabled = true
            binding.addNewCategoryBtn.setBackgroundResource(R.drawable.brow_btn_backround)
            progressAndSaveBtnOperation(1)
        }else{
            Toast.makeText(baseContext,"Category"+" "+binding.categoryNameInput+" "+"is Added",
                Toast.LENGTH_SHORT).show()
            binding.addNewCategoryBtn.isEnabled = true
            Toast.makeText(baseContext,"Please try Again!", Toast.LENGTH_SHORT).show()
            progressAndSaveBtnOperation(1)

        }

    }

    private fun progressAndSaveBtnOperation(i: Int) {
        if(i == 1){
            binding.addingProcess.visibility = View.GONE
            binding.addNewCategoryBtn.visibility = View.VISIBLE
            binding.addNewCategoryBtn.isEnabled = false
            binding.addNewCategoryBtn.setBackgroundResource(R.drawable.disable_btns_backroud)
        }else{
            binding.addingProcess.visibility = View.VISIBLE
            binding.addNewCategoryBtn.visibility = View.GONE
        }
    }

    private fun getCategoryImage() {
        binding.newCatedgoryImage.setOnClickListener {
            var userProfileintent =  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            categoryLunche?.launch(userProfileintent)
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),2)
            }else{

            }}
    }

    private fun userProfileLancherfun() {
        categoryLunche =registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri: Uri? = result.data?.data
                binding.newCatedgoryImage.setImageURI(selectedImageUri)
                if (selectedImageUri != null) {
                    newCategoryImage = selectedImageUri
                    categoryImageIsReady = true
                }
            }
        }
    }
}