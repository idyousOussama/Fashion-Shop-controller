package com.example.store_application_control.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.store_application.AppliactionObjs.User
import com.example.store_application_control.ApplicationObjs.Accounts
import com.example.store_application_control.R
import com.example.store_application_control.RoomDatabase.SupportViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Splash_Activity : AppCompatActivity() {
    lateinit var supportViewModel : SupportViewModel
var firebaseDB = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
   supportViewModel = ViewModelProvider(this).get(SupportViewModel::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            var supportAccount :Accounts = supportViewModel.getSupportAccount()
            if (supportAccount != null){
                checkAuthAccount(supportAccount)
            }else{
                goToSignInActtivity()
            }
        }
    }

    private fun checkAuthAccount(supportAccount : Accounts) {
        val accountAuth = FirebaseAuth.getInstance()
        accountAuth.signInWithEmailAndPassword(supportAccount.email , supportAccount.accountPassword).addOnCompleteListener { task ->
            if (task.isSuccessful){
                getSupportAccount()
            }else{
                goToSignInActtivity()
            }

        }
    }

    private fun goToSignInActtivity() {
        val intent = Intent (baseContext,SginIn_Activity::class.java)
        startActivity(intent)
    }

    private fun getSupportAccount() {
        var supportRef = firebaseDB.getReference("Supports")
        supportRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    for(item in p0.children){
                        var support = item.getValue(User::class.java)
                        if (support != null){
                            val intent = Intent(baseContext,MainActivity::class.java)
                            intent.putExtra("support",support)
                            startActivity(intent)
                        }
                    }
                }else{
                    goToSignInActtivity()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                goToSignInActtivity()
            }

        })

    }
}