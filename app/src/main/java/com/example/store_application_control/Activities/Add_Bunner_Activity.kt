package com.example.store_application_control.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store_application_control.ApplicationObjs.Bunner
import com.example.store_application_control.Listeners.BunnerImageListener
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityAddBunnerBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Add_Bunner_Activity : AppCompatActivity() {
    lateinit var binding : ActivityAddBunnerBinding
    var firebaseDB = FirebaseDatabase.getInstance()
    lateinit var bunnerImage : String
    lateinit var bunnerImageLaucher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding= ActivityAddBunnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBunnenImage()
        getBunnerImage()
        upLoadBunnerImage()
        goBack()
    }
    private fun goBack() {
        binding.newBunnerGoBack.setOnClickListener {
            finish()
        }
    }
    private fun upLoadBunnerImage() {
        binding.addNewBunnerBtn.setOnClickListener {
            if (bunnerImage != null){
                binding.addNewBunnerBtn.visibility = View.GONE
                binding.addNewBunnerProgress.visibility = View.VISIBLE
                binding.upLoadBunnerImageProgress.visibility = View.VISIBLE
                val firebaseStorageRef = FirebaseStorage.getInstance()
                val formate = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                var date = Date()
                val fileName = formate.format(date)
                val fileRef = firebaseStorageRef.getReference(fileName + "bunner")
                fileRef.putFile(bunnerImage!!.toUri()).addOnSuccessListener(object :
                    OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            addNewBunner(uri)
                        }
                    }

                }).addOnProgressListener {  taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    binding.upLoadBunnerImageProgress.progress = progress.toInt()

                }
            }
            else{
                Toast.makeText(baseContext,"Please add bunner image and try again." , Toast.LENGTH_SHORT).show()
            }
        }}
    private fun addNewBunner(bunnerIMG: Uri?) {
val bunnerRef = firebaseDB.getReference("Bunners")
        var bunnerId = bunnerRef.push().key.toString()
        val newBunner = Bunner(bunnerId,bunnerIMG.toString())
        bunnerRef.child(bunnerId).setValue(newBunner).addOnCompleteListener { task->

            if(task.isSuccessful){
                Toast.makeText(baseContext,"Bunner Added SuccessFull", Toast.LENGTH_SHORT).show()
            binding.addNewBunnerBtn.visibility = View.VISIBLE
            binding.addNewBunnerProgress.visibility = View.GONE

            }else{
                binding.addNewBunnerBtn.visibility = View.VISIBLE
                binding.addNewBunnerProgress.visibility = View.GONE
            }
        }
    }
    private fun getBunnerImage() {
        binding.AddBunnerImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            }else{
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                bunnerImageLaucher.launch(intent)
            }
        }

    }
    private fun setBunnenImage() {
        bunnerImageLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                var selectedImage : Uri? = result.data!!.data
                bunnerImage = selectedImage.toString()
                binding.AddBunnerImage.setImageURI(selectedImage)

            }
        }
    }
}