package com.example.store_application_control.Activities

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.store_application.AppliactionObjs.User
import com.example.store_application_control.R
import com.example.store_application_control.databinding.ActivitySginInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.store_application_control.ApplicationObjs.Accounts
import com.example.store_application_control.RoomDatabase.SupportRoom
import com.example.store_application_control.RoomDatabase.SupportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SginIn_Activity : AppCompatActivity() {
lateinit var binding : ActivitySginInBinding
    private var emailIsSuccessful= false
    private var passWordIsSuccessful= false
   lateinit var supportViewModel:SupportViewModel
    var firebaseDB = FirebaseDatabase.getInstance()
    var account : Account? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding = ActivitySginInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setContentView(binding.root)
        sginInClicked()
        setForgetEmail()
        signIn()
        showAndHidepassword()
    }

    private fun showAndHidepassword() {
        var isPasswordVisible = false

        binding.sginInShowPassword.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                binding.signInUserPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.sginInShowPassword.setImageResource(R.drawable.close_eyes)
                isPasswordVisible = false
            } else {
                // Show password
                binding.signInUserPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.sginInShowPassword.setImageResource(R.drawable.open_eyes)
                isPasswordVisible = true
            }
            // Move cursor to the end of the text
            binding.signInUserPassword.setSelection(binding.signInUserPassword.text.length)
        }
    }

    private fun setForgetEmail() {
        binding.forgetPassword.setOnClickListener{
            if(emailIsSuccessful){
                var intent = Intent(baseContext,ForgetActivity::class.java)
                intent.putExtra("forgetUserEmail" , binding.signInUseremail.text.toString())
                startActivity(intent)
            }else{
                Toast.makeText(baseContext,"Please Add Email", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signIn() {
        binding.signInBtn.setOnClickListener {
            if(emailIsSuccessful&&passWordIsSuccessful){
                initSginInBtn(1)
                checkAccountIsExists(binding.signInUseremail.text.toString(),binding.signInUserPassword.text.toString())
            }

        }
    }

    private fun initSginInBtn(i: Int) {
        if (i == 1){
            binding.signInBtn.visibility = View.GONE
            binding.sginInProgress.visibility = View.VISIBLE
        }else{
            binding.signInBtn.visibility = View.VISIBLE
            binding.sginInProgress.visibility = View.GONE
        }

    }

    private fun checkAccountIsExists(accountEmail : String ,accountPassword: String) {
        var firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(accountEmail , accountPassword).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val user  = firebaseAuth.currentUser
                val userUid = user?.uid.toString()
                accountFetshByUID(userUid)

            }else{
                initSginInBtn(2)
                var exception =task.exception
                when(exception){
                    is FirebaseAuthInvalidCredentialsException ->{
                              Toast.makeText(baseContext,"Please change your info and try again",Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseAuthInvalidUserException ->{
                     when(exception.errorCode){
                         "ERROR_USER_NOT_FOUND" ->{
                             Toast.makeText(baseContext,"Account Not foud",Toast.LENGTH_SHORT).show()
                         }
                         "ERROR_USER_DISABLED" ->{
                             Toast.makeText(baseContext,"this account is already disable",Toast.LENGTH_SHORT).show()
                         }
                     }
                    }
                } }
        }.addOnFailureListener {
            initSginInBtn(2)
        }
    }
    private fun accountFetshByUID(userUid: String) {
        var userRef = firebaseDB.getReference("Supports").child(userUid)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    initSginInBtn(2)
                    val user = p0.getValue(User::class.java)
                  checkSupportRoom(user!!.userAccount!!)
                }else{
                    initSginInBtn(2)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                initSginInBtn(2)
            }

        })
    }

    private fun checkSupportRoom(suppoprtAccount : Accounts) {
       supportViewModel = ViewModelProvider(this).get(SupportViewModel::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            supportViewModel.deleteSupportAccount()
            CoroutineScope(Dispatchers.Main).launch {
                supportViewModel.insertNewAccount(suppoprtAccount)
                var intent = Intent(baseContext,Splash_Activity::class.java)
                startActivity(intent)
            }


        }
    }

    private fun sginInClicked() {
        binding.signInUseremail.addTextChangedListener { TextWatcher ->
            var emailText : String = binding.signInUseremail.text.toString().trim()

            if (emailText.isEmpty() || !emailText.endsWith("@gmail.com")){
                binding.signInUseremail.setBackgroundResource(R.drawable.inpute_wrong_backround)

                emailIsSuccessful = false
                enableSginInBtn()
            }else{
                binding.signInUseremail.setBackgroundResource(R.drawable.white_input_text_backround_res)
                emailIsSuccessful =true
                enableSginInBtn()
            }
        }
        binding.signInUserPassword.addTextChangedListener { TextWatcher ->
            var passwordText : String = binding.signInUseremail.text.toString().trim()

            if (passwordText.isEmpty()){
                binding.signInUserPassword.setBackgroundResource(R.drawable.inpute_wrong_backround)
                passWordIsSuccessful = false
                enableSginInBtn()
            }else{
                binding.signInUserPassword.setBackgroundResource(R.drawable.white_input_text_backround_res)
                passWordIsSuccessful =true
                enableSginInBtn()
            }
        }
    }

    private fun enableSginInBtn() {
        if(passWordIsSuccessful && emailIsSuccessful){
            binding.signInBtn.isEnabled = true
            binding.singBtnLayout.setBackgroundResource(R.drawable.brow_btn_backround)
        }else{
            binding.signInBtn.isEnabled = false
            binding.singBtnLayout.setBackgroundResource(R.drawable.disable_btns_backroud)
        }
    }

}