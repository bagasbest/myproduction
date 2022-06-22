package com.project.myproduction.ui.obat_umum

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityHerbsAddEditBinding
import java.util.*

class HerbsAddEditActivity : AppCompatActivity() {

    private var binding: ActivityHerbsAddEditBinding? = null
    private var model: HerbsModel? = null
    private var size = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHerbsAddEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val option = intent.getStringExtra(OPTION)
        showDropdownSize()
        if (option == "add") {
            binding?.title?.text = "Tambahkan Obat Umum"
            binding?.saveBtn?.text = "Simpan Data"
        } else {
            binding?.title?.text = "Edit Obat Umum"
            binding?.saveBtn?.text = "Simpan Perubahan"
            model = intent.getParcelableExtra(EXTRA_DATA)
            size = model?.size.toString()
            binding?.name?.setText(model?.name)
            binding?.code?.setText(model?.code)
            binding?.type?.setText(model?.type)
            binding?.price?.setText(model?.price.toString())
            if(model?.pricePerSize != null) {
                binding?.pricePerSize?.setText(model?.pricePerSize.toString())
                binding?.stockSatuan?.setText(model?.stockPerSize.toString())
            } else {
                binding?.pricePerSize?.setText("0")
                binding?.stockSatuan?.setText("0")
            }
            binding?.stock?.setText(model?.stock.toString())
        }

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.type?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty()) {
                    val price = binding?.price?.text.toString()
                    val stock = binding?.stock?.text.toString()
                    val type = binding?.type?.text.toString()
                    if(price.isNotEmpty()) {
                        binding?.pricePerSize?.setText("${price.toLong() / type.toLong()}")
                    } else {
                        binding?.pricePerSize?.setText("0")
                    }

                    if(stock.isNotEmpty()) {
                        binding?.stockSatuan?.setText("${stock.toLong() * type.toLong()}")
                    } else {
                        binding?.stockSatuan?.setText("0")
                    }
                } else {
                    binding?.pricePerSize?.setText("0")
                    binding?.stockSatuan?.setText("0")
                }
            }

        })

        binding?.price?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty()) {
                    val price = binding?.price?.text.toString()
                    val type = binding?.type?.text.toString()
                    if(type.isNotEmpty()) {
                        binding?.pricePerSize?.setText("${price.toLong() / type.toLong()}")
                    } else {
                        binding?.pricePerSize?.setText("0")
                    }
                } else {
                    binding?.pricePerSize?.setText("0")
                }
            }

        })

        binding?.stock?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty()) {
                    val stock = binding?.stock?.text.toString()
                    val type = binding?.type?.text.toString()
                    if(type.isNotEmpty()) {
                        binding?.stockSatuan?.setText("${stock.toLong() * type.toLong()}")
                    } else {
                        binding?.stockSatuan?.setText("0")
                    }
                } else {
                    binding?.stockSatuan?.setText("0")
                }
            }

        })

        binding?.saveBtn?.setOnClickListener {
            val name = binding?.name?.text.toString().trim()
            val code = binding?.code?.text.toString().trim()
            val type = binding?.type?.text.toString().trim()
            val price = binding?.price?.text.toString().trim()
            val pricePerSize = binding?.pricePerSize?.text.toString().trim()
            val stock = binding?.stock?.text.toString().trim()
            val stockPerSize = binding?.stockSatuan?.text.toString().trim()


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
                pricePerSize.isEmpty() -> {
                    Toast.makeText(this, "Anda harus mengisi Ukuran dan Harga Obat", Toast.LENGTH_SHORT).show()
                }
                stock.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Jika stok tidak ada / kosong, silahkan tuliskan 0",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                stockPerSize.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Anda harus mengisi Ukuran dan Stok obat",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                size == "" -> {
                    Toast.makeText(
                        this,
                        "Anda harus memilih jenis kemasan obat",
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
                            "size" to size,
                            "price" to price.toLong(),
                            "pricePerSize" to pricePerSize.toLong(),
                            "stock" to stock.toLong(),
                            "stockPerSize" to stockPerSize.toLong(),
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
                            "size" to size,
                            "price" to price.toLong(),
                            "pricePerSize" to pricePerSize.toLong(),
                            "stock" to stock.toLong(),
                            "stockPerSize" to stockPerSize.toLong(),
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

    private fun showDropdownSize() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.size, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.size?.setAdapter(adapter)
        binding?.size?.setOnItemClickListener { _, _, _, _ ->
            size = binding?.size!!.text.toString()
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