package com.project.myproduction.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.myproduction.R
import com.project.myproduction.auth.LoginActivity
import com.project.myproduction.auth.RegisterActivity
import com.project.myproduction.databinding.ActivitySettingsBinding
import com.project.myproduction.ui.settings.cusotomer_data.CustomerDataActivity
import com.project.myproduction.ui.settings.managing_sales.ManagingSalesActivity

class SettingsActivity : AppCompatActivity() {

    private var binding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val role = intent.getStringExtra(ROLE)
        if(role == "admin") {
            binding?.addSales?.visibility = View.VISIBLE
            binding?.managingSales?.visibility = View.VISIBLE
        }

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.addSales?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding?.managingSales?.setOnClickListener {
            startActivity(Intent(this, ManagingSalesActivity::class.java))
        }

        binding?.signOut?.setOnClickListener {
            showConfirmLogout()
        }

        binding?.share?.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Berikut merupakan aplikasi SALES: ")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        binding?.customerData?.setOnClickListener {
            startActivity(Intent(this, CustomerDataActivity::class.java))
        }
    }

    private fun showConfirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin Sign-out dari aplikasi ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                signOut()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun signOut() {
        val prefs: SharedPreferences = getSharedPreferences(
            "myproduction", Context.MODE_PRIVATE
        )
        FirebaseAuth.getInstance().signOut()
        prefs.edit().remove("em").apply()
        prefs.edit().remove("pw").apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ROLE = "role"
    }
}