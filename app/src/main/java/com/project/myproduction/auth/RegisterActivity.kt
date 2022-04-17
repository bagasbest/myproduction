package com.project.myproduction.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

         binding?.register?.setOnClickListener {
             val prefs: SharedPreferences = getSharedPreferences(
                 "myproduction", Context.MODE_PRIVATE
             )
             FirebaseAuth.getInstance().signOut()
             val adminEmail = prefs.getString("em", "")
             val adminPassword = prefs.getString("pw", "")
             formValidation(adminEmail, adminPassword)
         }


    }

    private fun formValidation(adminEmail: String?, adminPassword: String?) {
        val name = binding?.name?.text.toString().trim()
        val username = binding?.username?.text.toString().trim()
        val email = binding?.email?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()
        val phone = binding?.phone?.text.toString().trim()
        val work = binding?.work?.text.toString().trim()

        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            username.isEmpty() -> {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            !email.contains("@") || !email.contains(".") -> {
                Toast.makeText(this, "Format email salah", Toast.LENGTH_SHORT).show()
                return
            }
            phone.isEmpty() -> {
                Toast.makeText(this, "No.Telepon tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            work.isEmpty() -> {
                Toast.makeText(this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.length < 6 -> {
                Toast.makeText(this, "Kata sandi minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return
            }
        }

        binding?.progressBar?.visibility = View.VISIBLE
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val userId = FirebaseAuth.getInstance().currentUser!!.uid

                    val data = mapOf(
                        "uid" to userId,
                        "name" to name,
                        "username" to username,
                        "email" to email,
                        "phone" to phone,
                        "role" to "sales",
                        "work" to work,
                        "status" to "Aktif",
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(userId)
                        .set(data)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseAuth.getInstance().signOut()

                                FirebaseAuth
                                    .getInstance()
                                    .signInWithEmailAndPassword(adminEmail!!, adminPassword!!)
                                    .addOnCompleteListener { result ->
                                        if (result.isSuccessful) {
                                            binding?.progressBar?.visibility = View.GONE
                                            showSuccessDialog()
                                        } else {
                                            binding?.progressBar?.visibility = View.GONE
                                            showFailureDialog("Silahkan mendaftar kembali dengan informasi yang benar, dan pastikan koneksi internet lancar")
                                        }
                                    }
                            } else {
                                binding?.progressBar?.visibility = View.GONE
                                showFailureDialog("Silahkan mendaftar kembali dengan informasi yang benar, dan pastikan koneksi internet lancar")
                            }
                        }
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    try {
                        throw it.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        showFailureDialog("Email yang anda daftarkan sudah digunakan, silahkan coba email lain")
                    } catch (e: java.lang.Exception) {
                        Log.e("TAG", e.message!!)
                    }
                }
            }
    }

    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan registrasi")
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// munculkan dialog ketika sukses registrasi
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil melakukan registrasi Sales")
            .setMessage("Silahkan berikan username dan kata sandi kepada sales yang bersangkutan!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}