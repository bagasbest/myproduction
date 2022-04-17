package com.project.myproduction.ui.settings.managing_sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityManagingSalesDetailBinding

class ManagingSalesDetailActivity : AppCompatActivity() {

    private var binding: ActivityManagingSalesDetailBinding? = null
    private var model: ManagingSalesModel? = null
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagingSalesDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)

        binding?.name?.setText(model?.name)
        binding?.email?.setText(model?.email)
        binding?.username?.setText(model?.username)
        binding?.work?.setText(model?.work)
        binding?.phone?.setText(model?.phone)

        dropdownStatus()

        binding?.saveBtn?.setOnClickListener {
            formValidation()
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun dropdownStatus() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.status, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.status?.setAdapter(adapter)
        binding?.status?.setOnItemClickListener { _, _, _, _ ->
            status = binding?.status!!.text.toString()
        }
    }

    private fun formValidation() {
        if(status == null) {
            Toast.makeText(this, "Silahkan pilih status terlebih dahulu", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model?.uid!!)
                .update("status", status)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Berhasil memperbarui status sales", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal memperbarui status sales, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}