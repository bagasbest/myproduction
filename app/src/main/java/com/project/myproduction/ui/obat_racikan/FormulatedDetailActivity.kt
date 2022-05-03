package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityFormulatedDetailBinding
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class FormulatedDetailActivity : AppCompatActivity() {

    private var binding: ActivityFormulatedDetailBinding? = null
    private var model: FormulatedModel? = null
    private var adapter: FormulatedMaterialAdapter? = null
    private var name: String? = null
    private var userId: String? = null
    private val formulatedQtyList = ArrayList<Long>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulatedDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        val formatter = DecimalFormat("#,###")


        model = intent.getParcelableExtra(EXTRA_DATA)
        checkRole()
        binding?.name?.setText(model?.name)
        binding?.code?.setText(model?.code)
        binding?.type?.setText(model?.type)
        binding?.price?.setText("Rp.${formatter.format(model?.price)}")
        initRecyclerView()

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, FormulatedAddEditActivity::class.java)
            intent.putExtra(FormulatedAddEditActivity.OPTION, "edit")
            intent.putExtra(FormulatedAddEditActivity.EXTRA_DATA, model)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            showConfirmDelete()
        }

        binding?.addProductBtn?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        val qtyProduct = binding?.qtyProduct?.text.toString().trim()
        if (qtyProduct.isEmpty() || qtyProduct.toInt() <= 0) {
            Toast.makeText(this, "Minimal pemesanan 1 produk", Toast.LENGTH_SHORT).show()
        } else {
            val materialList = ArrayList<String>()
            var stockCounter = 0
            materialList.clear()
            formulatedQtyList.clear()

            binding?.progressBar?.visibility = View.VISIBLE
            for (index in model?.material?.indices!!) {
                materialList.add(model?.material!![index].uid!!)
                formulatedQtyList.add(model?.material!![index].qty!!)
                FirebaseFirestore
                    .getInstance()
                    .collection("material")
                    .document(model?.material!![index].uid!!)
                    .get()
                    .addOnSuccessListener {
                        val currentStock = it.data!!["stock"] as Long

                        if (currentStock < model?.material!![index].qty?.times(qtyProduct.toLong())!!) {
                            Toast.makeText(
                                this,
                                "stok bahan baku ${model?.material!![index].name} tidak mencukupi",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding?.progressBar?.visibility = View.GONE
                            stockCounter++
                        }
                    }
            }

            Handler().postDelayed({
                if (stockCounter == 0) {
                    val uid = System.currentTimeMillis().toString()

                    val data = mapOf(
                        "uid" to uid,
                        "name" to model?.name,
                        "nameTemp" to model?.name?.lowercase(Locale.getDefault()),
                        "code" to model?.code,
                        "type" to model?.type,
                        "price" to model?.price!! * qtyProduct.toLong(),
                        "qty" to qtyProduct.toLong(),
                        "salesName" to name,
                        "salesId" to userId,
                        "productId" to model?.uid,
                        "materialId" to materialList,
                        "formulatedQty" to formulatedQtyList,
                        "category" to "formulated"
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("purchase_order")
                        .document(uid)
                        .set(data)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                /// potong stok
                                cutStock(qtyProduct.toLong())
                            } else {
                                binding?.progressBar?.visibility = View.GONE
                                showFailureDialog()
                            }
                        }
                }
            }, 1000)
        }
    }

    private fun cutStock(qty: Long) {
        for (index in model?.material?.indices!!) {
            FirebaseFirestore
                .getInstance()
                .collection("material")
                .document(model?.material!![index].uid!!)
                .get()
                .addOnSuccessListener {
                    val currentStock = it.data!!["stock"] as Long
                    val newStock = formulatedQtyList[index].times(qty)
                    val result = currentStock - newStock
                    FirebaseFirestore
                        .getInstance()
                        .collection("material")
                        .document(model?.material!![index].uid!!)
                        .update("stock", result)
                }
        }

        Handler().postDelayed({
            binding?.progressBar?.visibility = View.GONE
            showSuccessDialog()
        }, 3000)
    }

    private fun initRecyclerView() {
        binding?.rvMaterial?.layoutManager = LinearLayoutManager(this)
        adapter = FormulatedMaterialAdapter("detail", model?.material)
        binding?.rvMaterial?.adapter = adapter
    }

    private fun showConfirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Racikan")
            .setMessage("Apakah anda yakin ingin menghapus Racikan ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deleteData()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun deleteData() {
        FirebaseFirestore
            .getInstance()
            .collection("formulated")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Sukses menghapus obat", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal menghapus obat, terdapat kendala koneksi internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
                val role = "" + it.data!!["role"]
                name = "" + it.data!!["name"]
                userId = "" + it.data!!["uid"]
                if (role == "admin" || role == "sales") {
                    binding?.edit?.visibility = View.VISIBLE
                    binding?.delete?.visibility = View.VISIBLE
                }
            }
    }


    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menambahkan Ke Daftar Purchase Order")
            .setMessage("Ups, koneksi internet anda bermasalah, silahkan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menambahkan Ke Daftar Purchase Order")
            .setMessage("Silahkan cek menu PO untuk melakukan purchase order")
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
        const val EXTRA_DATA = "data"
    }
}