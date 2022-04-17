package com.project.myproduction.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.auth.LoginActivity
import com.project.myproduction.databinding.ActivityHomeBinding
import com.project.myproduction.ui.settings.SettingsActivity

class HomeActivity : AppCompatActivity() {

    private var binding: ActivityHomeBinding? = null
    private var role = ""

    override fun onResume() {
        super.onResume()
        checkRole()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.settingBtn?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(SettingsActivity.ROLE, role)
            startActivity(intent)
        }

    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                role = "" + it.data!!["role"]
                if("" + it.data!!["status"] == "Blokir") {
                    showBlockAlert(uid)
                }
            }
    }

    private fun showBlockAlert(uid: String) {
        AlertDialog.Builder(this)
            .setMessage("Mohon maaf, akun anda sedang di blokir oleh admin, silahkan konsultasi dengan admin untuk membuka blokir\n\nTerima kasih.")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val prefs: SharedPreferences = getSharedPreferences(
                    "myproduction", Context.MODE_PRIVATE
                )
                FirebaseAuth.getInstance().signOut()
                prefs.edit().putBoolean(uid, true).apply()

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}