package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityFormulatedDetailBinding
import java.text.DecimalFormat
import java.util.*

class FormulatedDetailActivity : AppCompatActivity() {

    private var binding: ActivityFormulatedDetailBinding? = null
    private var model: FormulatedModel? = null
    private var adapter: FormulatedMaterialAdapter? = null
    private var name: String? = null
    private var userId: String? = null

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
            binding?.progressBar?.visibility = View.VISIBLE
            for (index in model?.material?.indices!!) {
                materialList.add(model?.material!![index].uid!!)
                if (model?.material!![index].stock!! < model?.material!![index].qty?.times(
                        qtyProduct.toLong()
                    )!!
                ) {
                    Toast.makeText(
                        this,
                        "stok bahan baku ${model?.material!![index].name} tidak mencukupi",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.progressBar?.visibility = View.GONE
                    return
                }
            }

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
                "productStock" to model?.stock,
                "category" to "formulated"
            )

            FirebaseFirestore
                .getInstance()
                .collection("purchase_order")
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
        }
    }

    private fun reduceMaterial(qtyProduct: Long) {
        for(index in model?.material?.indices!!) {
            val materialId = model?.material!![index].uid
            val newStock = model?.material!![index].stock!! - model?.material!![index].qty!!*qtyProduct
            FirebaseFirestore
                .getInstance()
                .collection("material")
                .document(materialId!!)
                .update("stock", newStock)
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

    @SuppressLint("SetTextI18n")
    private fun addStock() {
        val currentStock = model?.stock
        val stockEt: TextInputEditText
        val confirmBtn: Button
        val pb: ProgressBar
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_add_stock)
        stockEt = dialog.findViewById(R.id.stock)
        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        pb = dialog.findViewById(R.id.progressBar)

        confirmBtn?.setOnClickListener {
            val stock = stockEt.text.toString().trim()

            if (stock.isEmpty() || stock.toLong() <= 0) {
                Toast.makeText(this, "Maaf, stok minimal 1", Toast.LENGTH_SHORT).show()
            } else {
                pb.visibility = View.VISIBLE
                if (currentStock != null) {
                    FirebaseFirestore
                        .getInstance()
                        .collection("formulated")
                        .document(model?.uid!!)
                        .update("stock", currentStock + stock.toLong())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialog.dismiss()
                                Toast.makeText(this, "Sukses mengupdate stok", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                dialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Gagal mengupdate stok, silahkan periksa koneksi internet anda dan coba lagi nanti",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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