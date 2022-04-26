package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityFormulatedAddEditBinding
import com.project.myproduction.ui.obat_racikan.material.MaterialModel
import com.project.myproduction.ui.obat_racikan.material.MaterialViewModel
import java.util.*
import kotlin.collections.ArrayList

class FormulatedAddEditActivity : AppCompatActivity(), IFirebaseLoadDone  {

    private var binding: ActivityFormulatedAddEditBinding? = null
    private var model: FormulatedModel? = null
    private var adapter: FormulatedMaterialAdapter?= null
    private var materialList: List<MaterialModel> = ArrayList()
    private var listOfMaterial = ArrayList<MaterialModel>()
    private lateinit var iFirebaseLoadDone: IFirebaseLoadDone

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulatedAddEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        iFirebaseLoadDone = this
        getAllMaterial()

        val option = intent.getStringExtra(OPTION)
        if (option == "add") {
            binding?.title?.text = "Buat Racikan Baru"
            binding?.saveBtn?.text = "Simpan Racikan Ini"
        } else {
            binding?.title?.text = "Edit Racikan"
            binding?.saveBtn?.text = "Simpan Perubahan Racikan"
            model = intent.getParcelableExtra(EXTRA_DATA)
            binding?.name?.setText(model?.name)
            binding?.code?.setText(model?.code)
            binding?.type?.setText(model?.type)
            binding?.price?.setText(model?.price.toString())
            binding?.stock?.setText(model?.stock.toString())
            listOfMaterial.addAll(model?.material!!)
            initRecyclerView("edit")
        }

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation(option)
        }


        binding?.searchableSpinner?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val material = materialList[p2]
                    val name = material.name
                    val code = material.code
                    val type = material.type
                    val uid = material.uid
                    val stock = material.stock

                    val model = MaterialModel()
                    model.name = name
                    model.uid = uid
                    model.code = code
                    model.type = type
                    model.stock = stock

                    listOfMaterial.add(model)

                   initRecyclerView("add")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            })
    }

    private fun formValidation(option: String?) {
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
            listOfMaterial.size == 0 -> {
                Toast.makeText(this, "Komposisi racikan tidak boleh kosong, minimal 1 bahan baku di pilih", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding?.progressBar?.visibility = View.VISIBLE

                if (option == "add") {
                    val uid = System.currentTimeMillis().toString()

                    for(idx in listOfMaterial.indices) {
                        if(listOfMaterial[idx].qty == 0L) {
                            Toast.makeText(this, "Kuantitas bahan baku minimal 1", Toast.LENGTH_SHORT).show()
                            binding?.progressBar?.visibility = View.GONE
                            return
                        }
                    }

                    val data = mapOf(
                        "uid" to uid,
                        "name" to name,
                        "nameTemp" to name.lowercase(Locale.getDefault()),
                        "code" to code,
                        "type" to type,
                        "price" to price.toLong(),
                        "stock" to stock.toLong(),
                        "material" to listOfMaterial
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("formulated")
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

                    for(idx in listOfMaterial.indices) {
                        if(listOfMaterial[idx].qty == 0L) {
                            Toast.makeText(this, "Kuantitas bahan baku minimal 1", Toast.LENGTH_SHORT).show()
                            binding?.progressBar?.visibility = View.GONE
                            return
                        }
                    }

                    val data = mapOf(
                        "name" to name,
                        "nameTemp" to name.lowercase(Locale.getDefault()),
                        "code" to code,
                        "type" to type,
                        "price" to price.toLong(),
                        "stock" to stock.toLong(),
                        "material" to listOfMaterial
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("formulated")
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

    private fun getAllMaterial() {
        val viewModel = ViewModelProvider(this)[MaterialViewModel::class.java]

        viewModel.setListMaterial()
        viewModel.getMaterial().observe(this) { materialList ->
            if (materialList.size > 0) {
                iFirebaseLoadDone.onFirebaseLoadSuccess(materialList)
            }
        }
    }

    private fun initRecyclerView(option: String) {
        if(option == "add") {
            binding?.rvMaterial?.layoutManager = LinearLayoutManager(this)
            adapter = FormulatedMaterialAdapter("add", listOfMaterial)
            binding?.rvMaterial?.adapter = adapter
        } else {
            binding?.rvMaterial?.layoutManager = LinearLayoutManager(this)
            adapter = FormulatedMaterialAdapter("edit",listOfMaterial)
            binding?.rvMaterial?.adapter = adapter
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

    override fun onFirebaseLoadSuccess(property: List<MaterialModel>) {
        this.materialList = property
        val propertyName = getMaterialNameList(property)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, propertyName)
        binding?.searchableSpinner?.adapter = adapter
    }

    private fun getMaterialNameList(materialList: List<MaterialModel>): List<String> {
        val result = ArrayList<String>()
        for (material in materialList) {
            result.add(material.name!!)
        }
        return result
    }

    override fun onFirebaseLoadFailed(message: String) {

    }
}