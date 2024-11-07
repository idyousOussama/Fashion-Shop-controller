package com.example.store_application_control.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.store_application_control.ApplicationObjs.Bunner
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivityUpDateBunnerBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpDateBunner_Activity : AppCompatActivity() {
lateinit var binding : ActivityUpDateBunnerBinding
    var upDateItemBunner: Bunner? = null
    var newBunnerImage :String? = null
    lateinit var bunnerImageLaucher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding = ActivityUpDateBunnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUpDateBunner()
getNewBunnerImage()
        setBunnenImage()
        upLoadNewBunnerImage()
        goBack()

    }
    private fun goBack() {
        binding.upDateBunnerGoBack.setOnClickListener {
            finish()
        }
    }
    private fun upLoadNewBunnerImage() {
binding.updateBunnerBtn.setOnClickListener {
    if (newBunnerImage != null){
    val firebaseStorageRef = FirebaseStorage.getInstance()
       val formate = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        var date = Date()
        val fileName = formate.format(date)
        val fileRef = firebaseStorageRef.getReference(fileName + "bunner")
fileRef.putFile(newBunnerImage!!.toUri()).addOnSuccessListener(object :
    OnSuccessListener<UploadTask.TaskSnapshot> {
    override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
        fileRef.downloadUrl.addOnSuccessListener { uri ->
            upDateBunner(uri)
        }
    }

})
    }else{
        Toast.makeText(baseContext,"Please upDate bunner image and try again." , Toast.LENGTH_SHORT).show()
    }
}
    }

    private fun upDateBunner(uri: Uri?) {
val bunnerRef = FirebaseDatabase.getInstance().getReference("Bunners")
    var bunnerUpDates = mapOf<String ,String>(
        "bunnerImage" to uri.toString()
    )
        bunnerRef.child(upDateItemBunner!!.id).updateChildren(bunnerUpDates).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(baseContext,"Bunner Update successful." , Toast.LENGTH_SHORT).show()
            val intent = Intent(baseContext ,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(baseContext,"Bunner Update Feiled . PleaseTry Again" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setBunnenImage() {
        bunnerImageLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                var selectedImage : Uri? = result.data!!.data
                newBunnerImage = selectedImage.toString()
                binding.upDateBunnerImage.setImageURI(selectedImage)

            }
        }
    }

    private fun getNewBunnerImage() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
       val intent = Intent (Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            bunnerImageLaucher.launch(intent)
        }else{
            val intent = Intent (Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            bunnerImageLaucher.launch(intent)
        }
    }

    private fun getUpDateBunner() {
        val bunnerIntent = intent
        upDateItemBunner = bunnerIntent.getSerializableExtra("updatedBunner") as? Bunner
        if (upDateItemBunner != null){
Picasso.get().load(upDateItemBunner!!.bunnerImage.toUri()).into(binding.upDateBunnerImage)
        }
    }
}