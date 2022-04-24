package com.project.myproduction.ui.obat_umum

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityHerbsAddEditBinding
import java.util.*

class HerbsAddEditActivity : AppCompatActivity() {

    private var binding: ActivityHerbsAddEditBinding? = null
    private var model: HerbsModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHerbsAddEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val option = intent.getStringExtra(OPTION)
        if (option == "add") {
            binding?.title?.text = "Tambahkan Obat Umum"
            binding?.saveBtn?.text = "Simpan Data"
        } else {
            binding?.title?.text = "Edit Obat Umum"
            binding?.saveBtn?.text = "Simpan Perubahan"
            model = intent.getParcelableExtra(EXTRA_DATA)
            binding?.name?.setText(model?.name)
            binding?.code?.setText(model?.code)
            binding?.type?.setText(model?.type)
            binding?.price?.setText(model?.price.toString())
            binding?.stock?.setText(model?.stock.toString())
        }

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            val name = binding?.name?.text.toString().trim()
            val code = binding?.code?.text.toString().trim()
            val type = binding?.type?.text.toString().trim()
            val price = binding?.price?.text.toString().trim()
            val stock = binding?.stock?.text.toString().trim()

            when {
                name.isEmpty() -> {
                    Toast.makeText(this, "Nama Obat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                code.isEmpty() -> {
                    Toast.makeText(this, "Kode Obat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                type.isEmpty() -> {
                    Toast.makeText(this, "Jenis/Ukuran Obat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                price.isEmpty() -> {
                    Toast.makeText(this, "Harga Obat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                stock.isEmpty() || stock.toInt() <= 0 -> {
                    Toast.makeText(
                        this,
                        "Stok Obat tidak boleh kosong, minimal 1 stok",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    binding?.progressBar?.visibility = View.VISIBLE

                    if (option == "add") {
                        val uid = System.currentTimeMillis().toString()

                        val data = mapOf(
                            "uid" to uid,
                            "name" to name,
                            "nameTemp" to name.lowercase(Locale.getDefault()),
                            "code" to code,
                            "type" to type,
                            "price" to price.toLong(),
                            "stock" to stock.toLong(),
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("common_herbs")
                            .document(uid)
                            .set(data)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    binding?.progressBar?.visibility = View.GONE
                                    showSuccessDialog()
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showFailureDialog()
                                }
                            }
                    } else {

                        val data = mapOf(
                            "name" to name,
                            "nameTemp" to name.lowercase(Locale.getDefault()),
                            "code" to code,
                            "type" to type,
                            "price" to price.toLong(),
                            "stock" to stock.toLong(),
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("common_herbs")
                            .document(model?.uid!!)
                            .update(data)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    binding?.progressBar?.visibility = View.GONE
                                    showSuccessDialog()
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showFailureDialog()
                                }
                            }
                    }
                }
            }
        }
    }


    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menyimpan Data Obat")
            .setMessage("Ups, koneksi internet anda bermasalah, silahkan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menyimpan Data Obat")
            .setMessage("Produk ini siap di terbitkan di katalog obat umum")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val OPTION = "option"
        const val EXTRA_DATA ="data"
    }
}