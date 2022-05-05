package com.project.myproduction.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.auth.LoginActivity
import com.project.myproduction.databinding.ActivityHomeBinding
import com.project.myproduction.ui.invoice.InvoiceActivity
import com.project.myproduction.ui.item_history.ItemHistoryActivity
import com.project.myproduction.ui.obat_racikan.FormulatedActivity
import com.project.myproduction.ui.obat_umum.SingleHerbsActivity
import com.project.myproduction.ui.purchase_order.POActivity
import com.project.myproduction.ui.purchase_order.PODetailActivity
import com.project.myproduction.ui.settings.SettingsActivity
import com.project.myproduction.ui.surat_jalan.SuratJalanActivity

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

        initView()

        binding?.settingBtn?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(SettingsActivity.ROLE, role)
            startActivity(intent)
        }

        binding?.singleHerb?.setOnClickListener {
            val intent = Intent(this, SingleHerbsActivity::class.java)
            intent.putExtra(SingleHerbsActivity.ROLE, role)
            startActivity(intent)
        }

        binding?.formulatedHerbs?.setOnClickListener {
           startActivity(Intent(this, FormulatedActivity::class.java))
        }

        binding?.purchaseOrder?.setOnClickListener {
            startActivity(Intent(this, PODetailActivity::class.java))
        }

        binding?.invoice?.setOnClickListener {
            startActivity(Intent(this, InvoiceActivity::class.java))
        }

        binding?.itemHistory?.setOnClickListener {
            startActivity(Intent(this, ItemHistoryActivity::class.java))
        }

        binding?.travelDocument?.setOnClickListener {
            startActivity(Intent(this, SuratJalanActivity::class.java))
        }

    }

    private fun initView() {
        Glide.with(this)
            .load(R.drawable.obat_umum)
            .into(binding!!.img1)

        Glide.with(this)
            .load(R.drawable.obat_racikan)
            .into(binding!!.img2)

        Glide.with(this)
            .load(R.drawable.purchase_order)
            .into(binding!!.img3)
    }

    @SuppressLint("SetTextI18n")
    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                role = "" + it.data!!["role"]
                val name = "" + it.data!!["name"]

                if("" + it.data!!["status"] != "Blokir" && role == "admin") {
                    binding?.textView8?.text = "Selamat datang $name, selamat bekerja :)"
                    binding?.gridLayout2?.visibility = View.VISIBLE
                    showImage()
                } else if ("" + it.data!!["status"] != "Blokir" && role == "sales") {
                    binding?.textView8?.text = "Selamat datang $name, selamat bekerja :)"
                } else {
                    showBlockAlert()
                }
            }
    }

    private fun showImage() {
        Glide.with(this)
            .load(R.drawable.item_history)
            .into(binding!!.img4)

        Glide.with(this)
            .load(R.drawable.invoice)
            .into(binding!!.img5)

        Glide.with(this)
            .load(R.drawable.surat_jalan)
            .into(binding!!.img6)
    }

    private fun showBlockAlert() {
        AlertDialog.Builder(this)
            .setMessage("Mohon maaf, akun anda sedang di blokir oleh admin, silahkan konsultasi dengan admin untuk membuka blokir\n\nTerima kasih.")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                FirebaseAuth.getInstance().signOut()

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