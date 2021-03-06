package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityFormulatedAddEditBinding
import com.project.myproduction.ui.obat_racikan.material.MaterialModel
import com.project.myproduction.ui.obat_racikan.material.MaterialViewModel
import com.project.myproduction.ui.obat_umum.HerbsModel
import com.project.myproduction.ui.obat_umum.HerbsViewModel
import java.util.*
import kotlin.collections.ArrayList

class FormulatedAddEditActivity : AppCompatActivity(), IFirebaseLoadDone  {

    private var binding: ActivityFormulatedAddEditBinding? = null
    private var model: FormulatedModel? = null
    private var adapter: FormulatedMaterialAdapter?= null
    private var materialList: List<MaterialModel> = ArrayList()
    private var herbsList: List<HerbsModel> = ArrayList()
    private var listOfMaterial = ArrayList<MaterialModel>()
    private lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private var materialOrCommon = ""
    private var option: String? = null
    private var size = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulatedAddEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        iFirebaseLoadDone = this

        option = intent.getStringExtra(OPTION)
        showDropdownSize()
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
            listOfMaterial.addAll(model?.material!!)
            initRecyclerView("edit")
        }

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation(option)
        }

        binding?.materialBtn?.setOnClickListener {
            materialOrCommon = "material"
            getAllMaterial()
            binding?.materialBtn?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            binding?.commonBtn?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
            binding?.searchableSpinner?.visibility = View.VISIBLE
        }

        binding?.commonBtn?.setOnClickListener {
            materialOrCommon = "common"
            getAllMaterial()
            binding?.materialBtn?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
            binding?.commonBtn?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            binding?.searchableSpinner?.visibility = View.VISIBLE
        }

        binding?.searchableSpinner?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    /// ketika berganti ke bahan umum / racikan ada sedikit kendala dimana otomatis menyimpan index 0
                    binding?.price?.setText("0")
                    if(materialOrCommon == "material") {
                        val material = materialList[p2]
                        val name = material.name
                        val code = material.code
                        val type = material.type
                        val pricePerSize = material.pricePerSize
                        val stockPerSize = material.stockPerSize
                        val uid = material.uid
                        val size = material.size

                        val model = MaterialModel()
                        model.name = name
                        model.uid = uid
                        model.code = code
                        model.pricePerSize = pricePerSize
                        model.stockPerSize = stockPerSize
                        model.type = type
                        model.size = size
                        model.collection = "material"

                        listOfMaterial.add(model)

                        if(option == "add") {
                            initRecyclerView("add")
                        } else {
                            initRecyclerView("edit")
                        }
                    } else {
                        val material = herbsList[p2]
                        val name = material.name
                        val code = material.code
                        val type = material.type
                        val pricePerSize = material.pricePerSize
                        val stockPerSize = material.stockPerSize
                        val uid = material.uid
                        val size = material.size

                        val model = MaterialModel()
                        model.name = name
                        model.uid = uid
                        model.code = code
                        model.type = type
                        model.pricePerSize = pricePerSize
                        model.stockPerSize = stockPerSize
                        model.size = size
                        model.collection = "common_herbs"

                        listOfMaterial.add(model)

                        if(option == "add") {
                            initRecyclerView("add")
                        } else {
                            initRecyclerView("edit")
                        }
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            })

        binding?.updatePriceTotal?.setOnClickListener {
           updatePriceTotal()
        }

        binding?.updatePriceTotal2?.setOnClickListener {
            updatePriceTotal()
        }
    }

    private fun updatePriceTotal() {
        var totalPrice = 0L
        for(i in listOfMaterial.indices) {
            val itemPrice : Long? = listOfMaterial[i].qty?.times(listOfMaterial[i].pricePerSize!!)
            totalPrice += itemPrice!!
        }
        binding?.price?.setText(totalPrice.toString())
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

    private fun formValidation(option: String?) {
        val name = binding?.name?.text.toString().trim()
        val code = binding?.code?.text.toString().trim()
        val type = binding?.type?.text.toString().trim()
        val price = binding?.price?.text.toString().trim()

        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Nama Racikan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            code.isEmpty() -> {
                Toast.makeText(this, "Kode Racikan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            type.isEmpty() -> {
                Toast.makeText(this, "Jenis Racikan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            size == "" -> {
                Toast.makeText(this, "Ukuran Racikan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            price.isEmpty() -> {
                Toast.makeText(this, "Harga Racikan tidak boleh kosong", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "Kuantitas bahan baku ''${listOfMaterial[idx].name}'' minimal 1", Toast.LENGTH_SHORT).show()
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
                        "size" to size,
                        "price" to price.toLong(),
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
                            Toast.makeText(this, "Kuantitas bahan baku ''${listOfMaterial[idx].name}'' minimal 1", Toast.LENGTH_SHORT).show()
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
        if(materialOrCommon == "material") {
            val viewModel = ViewModelProvider(this)[MaterialViewModel::class.java]

            viewModel.setListMaterial()
            viewModel.getMaterial().observe(this) { materialList ->
                if (materialList.size > 0) {
                    iFirebaseLoadDone.onFirebaseLoadSuccess(materialList)
                }
            }
        } else {
            val viewModel = ViewModelProvider(this)[HerbsViewModel::class.java]

            viewModel.setListCommonHerbs()
            viewModel.getHerbs().observe(this) { herbsList ->
                if (herbsList.size > 0) {
                    iFirebaseLoadDone.onFirebaseLoadSuccessCommon(herbsList)
                }
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
            .setTitle("Gagal Menyimpan Data Racikan")
            .setMessage("Ups, koneksi internet anda bermasalah, silahkan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menyimpan Data Racikan")
            .setMessage("Produk Racikan ini siap di terbitkan di katalog obat racikan")
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

    override fun onFirebaseLoadSuccessCommon(property: List<HerbsModel>) {
        this.herbsList = property
        val propertyName = getCommonHerbNameList(property)
        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, propertyName)
        binding?.searchableSpinner?.adapter = adapter
    }

    private fun getCommonHerbNameList(property: List<HerbsModel>): List<String> {
        val result = ArrayList<String>()
        for (herb in property) {
            result.add(herb.name!!)
        }
        return result
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