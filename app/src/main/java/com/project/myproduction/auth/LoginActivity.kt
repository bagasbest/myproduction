package com.project.myproduction.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityLoginBinding
import com.project.myproduction.ui.HomeActivity

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val prefs: SharedPreferences = getSharedPreferences(
            "myproduction", Context.MODE_PRIVATE
        )
        intent.getStringExtra(STATUS).also {
            if (it != null) {
                status = it
            }
        }
        autoLogin()

        binding?.login?.setOnClickListener {
            formValidation(prefs)
        }
    }

    private fun autoLogin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            if(status != "Block") {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                showBlockAlert()
            }
        }
    }

    private fun showBlockAlert() {
        AlertDialog.Builder(this)
            .setMessage("Mohon maaf, akun anda sedang di blokir oleh admin, silahkan konsultasi dengan admin untuk membuka blokir\n\nTerima kasih.")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun formValidation(prefs: SharedPreferences) {
        val username = binding?.username?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()

        when {
            username.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Maaf, Email atau No.Handphone tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Maaf, Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding?.progressBar?.visibility = View.VISIBLE


                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .whereEqualTo("username", username)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.size() > 0) {
                            for (document in documents) {
                                val email = "" + document.data["email"]

                                FirebaseAuth
                                    .getInstance()
                                    .signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {

                                            if(username == "admin") {
                                                prefs.edit().putString("em", email).apply()
                                                prefs.edit().putString("pw", password).apply()
                                            }


                                            if(status != "Block") {
                                                binding?.progressBar?.visibility = View.GONE
                                                startActivity(Intent(this, HomeActivity::class.java))
                                                finish()
                                            } else {
                                                binding?.progressBar?.visibility = View.GONE
                                                showBlockAlert()
                                            }

                                        } else {
                                            binding?.progressBar?.visibility = View.GONE
                                            showFailureDialog("Akun tidak terdaftar")
                                        }
                                    }
                            }
                        } else {
                            binding?.progressBar?.visibility = View.GONE
                            showFailureDialog("Mohon maaf, Akun tidak terdaftar")
                        }
                    }
            }
        }
    }

    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Gagal Login")
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val STATUS = "status"
    }
}