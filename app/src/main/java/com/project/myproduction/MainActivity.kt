package com.project.myproduction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.auth.LoginActivity
import com.project.myproduction.databinding.ActivityMainBinding
import com.project.myproduction.ui.HomeActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkIsUserBlockedOrNot()

        Timer().schedule(1500) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.putExtra(LoginActivity.STATUS, status)
            startActivity(intent)
            finish()
        }

    }

    private fun checkIsUserBlockedOrNot() {
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    status = "" + it.data!!["role"]
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}