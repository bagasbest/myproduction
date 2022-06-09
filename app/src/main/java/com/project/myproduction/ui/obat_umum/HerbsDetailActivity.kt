package com.project.myproduction.ui.obat_umum

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityHerbsDetailBinding
import com.project.myproduction.ui.obat_racikan.material.MaterialAddEditActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HerbsDetailActivity : AppCompatActivity() {

    private var binding: ActivityHerbsDetailBinding? = null
    private var model : HerbsModel? = null
    private var name: String? = null
    private var userId: String? = null
    private var poId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHerbsDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val formatter = DecimalFormat("#,###")


        model = intent.getParcelableExtra(EXTRA_DATA)
        checkRole()
        binding?.name?.setText(model?.name)
        binding?.code?.setText(model?.code)
        binding?.type?.setText(model?.type)
        binding?.price?.setText("Rp.${formatter.format(model?.price)}")
        binding?.stock?.setText(model?.stock.toString())

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, HerbsAddEditActivity::class.java)
            intent.putExtra(MaterialAddEditActivity.OPTION, "edit")
            intent.putExtra(HerbsAddEditActivity.EXTRA_DATA, model)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            showConfirmDelete()
        }

        binding?.addStock?.setOnClickListener {
            addStock()
        }

        binding?.addProductBtn?.setOnClickListener {
            checkIsThisProductAddedBeforeOrNot()
        }

        binding?.stockTaking?.setOnClickListener {
            stockTaking()
        }
    }

    private fun checkIsThisProductAddedBeforeOrNot() {
        FirebaseFirestore
            .getInstance()
            .collection("purchase_order")
            .whereEqualTo("productId", model?.uid)
            .whereEqualTo("salesId", userId)
            .get()
            .addOnSuccessListener {
                if(it.size() == 0) {
                    formValidation()
                } else {
                    Toast.makeText(this, "Anda sudah menambahkan produk ini pada purchase order, produk tidak boleh duplikat, silahkan hapus produk di PO jika ada yang salah", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun stockTaking() {
        val currentStock = model?.stock
        val stockEt: TextInputEditText
        val confirmBtn: Button
        val pb: ProgressBar
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_stock_taking)
        stockEt = dialog.findViewById(R.id.stock)
        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        pb = dialog.findViewById(R.id.progressBar)

        confirmBtn?.setOnClickListener {
            val stock = stockEt.text.toString().trim()

            if(stock.isEmpty() || stock.toLong() <= 0) {
                Toast.makeText(this, "Maaf, stok minimal 1", Toast.LENGTH_SHORT).show()
            } else {
                if (currentStock!! > 0) {
                    pb.visibility = View.VISIBLE
                    val calendar = Calendar.getInstance()
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = sdf.format(calendar.time)
                    val dateInMillis = Date().time

                    val uid = System.currentTimeMillis().toString()
                    val data = mapOf(
                        "uid" to uid,
                        "stock" to stock.toLong(),
                        "status" to "Stock-taking",
                        "date" to date,
                        "dateInMillis" to dateInMillis,
                        "productName" to model?.name,
                        "productId" to model?.uid,
                        "productCode" to model?.code,
                        "productType" to model?.type,
                        "customerName" to "Stock-taking",
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("item_history")
                        .document(uid)
                        .set(data)
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                cutStock(stock, dialog, pb)
                            } else {
                                dialog.dismiss()
                                pb.visibility = View.GONE
                                Toast.makeText(this, "Gagal mengambil stok, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else{
                    Toast.makeText(this, "Stok tidak mencukupi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun cutStock(stock: String, dialog: Dialog, pb: ProgressBar) {
        val resultStock = binding?.stock?.text.toString().toLong() - stock.toLong()
        FirebaseFirestore
            .getInstance()
            .collection("common_herbs")
            .document(model?.uid!!)
            .update("stock", resultStock)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    dialog.dismiss()
                    binding?.stock?.setText(resultStock.toString())
                    pb.visibility = View.GONE
                    Toast.makeText(this, "Sukses mengambil stok, log ini akan masuk item history", Toast.LENGTH_SHORT).show()
                } else {
                    dialog.dismiss()
                    pb.visibility = View.GONE
                    Toast.makeText(this, "Gagal mengambil stok, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun formValidation() {
        val qtyProduct = binding?.qtyProduct?.text.toString().trim()

        if(qtyProduct.isEmpty() || qtyProduct.toInt() <= 0) {
            Toast.makeText(this, "Minimal pemesanan 1 produk", Toast.LENGTH_SHORT).show()
        } else {
            val materialList = ArrayList<String>()
            binding?.progressBar?.visibility = View.VISIBLE

            if(model?.stock!! < qtyProduct.toLong()) {
                Toast.makeText(
                    this,
                    "stok obat ${model?.name} tidak mencukupi",
                    Toast.LENGTH_SHORT
                ).show()
                binding?.progressBar?.visibility = View.GONE
                return
            }
            materialList.add(model?.uid!!)

            poId = System.currentTimeMillis().toString()

            val data = mapOf(
                "uid" to poId,
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
                "category" to "common"
            )

            FirebaseFirestore
                .getInstance()
                .collection("purchase_order")
                .document(poId!!)
                .set(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        reduceStock(qtyProduct.toLong())
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        showFailureDialog()
                    }
                }
        }
    }

    private fun reduceStock(qtyProduct: Long) {
        val newStock = model?.stock!! - qtyProduct
        FirebaseFirestore
            .getInstance()
            .collection("common_herbs")
            .document(model?.uid!!)
            .update("stock", newStock)

        Handler().postDelayed({
            outgoingStock(qtyProduct)
        }, 3000)


    }

    private fun outgoingStock(qtyProduct: Long) {

        /// outgoing stock
        val calendar = Calendar.getInstance()
        val sdf2 = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outgoingDate = sdf2.format(calendar.time)
        val dateInMillis = Date().time

        val uid = System.currentTimeMillis().toString()
        val data = mapOf(
            "uid" to uid,
            "stock" to qtyProduct,
            "status" to "Outgoing",
            "date" to outgoingDate,
            "dateInMillis" to dateInMillis,
            "productName" to model?.name,
            "productId" to model?.uid,
            "productCode" to model?.code,
            "productType" to model?.type,
            "customerName" to "Outgoing",
        )

        FirebaseFirestore
            .getInstance()
            .collection("item_history")
            .document(poId!!)
            .set(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding?.progressBar?.visibility = View.GONE
                    showSuccessDialog()
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    showFailureDialog()
                }
            }
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

            if(stock.isEmpty() || stock.toLong() <= 0) {
                Toast.makeText(this, "Maaf, stok minimal 1", Toast.LENGTH_SHORT).show()
            } else {
                pb.visibility = View.VISIBLE
                if (currentStock != null) {
                    FirebaseFirestore
                        .getInstance()
                        .collection("common_herbs")
                        .document(model?.uid!!)
                        .update("stock", currentStock+stock.toLong())
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                binding?.stock?.setText("${currentStock+stock.toLong()}")
                                incomingStock(dialog, pb, stock)
                            } else {
                                dialog.dismiss()
                                Toast.makeText(this, "Gagal mengupdate stok, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun incomingStock(dialog: Dialog, pb: ProgressBar, stock: String) {
        val uid = System.currentTimeMillis().toString()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.format(calendar.time)
        val dateInMillis = Date().time

        val data = mapOf(
            "uid" to uid,
            "stock" to stock.toLong(),
            "status" to "Incoming",
            "date" to date,
            "dateInMillis" to dateInMillis,
            "productName" to model?.name,
            "productId" to model?.uid,
            "productCode" to model?.code,
            "productType" to model?.type,
            "customerName" to "Incoming",
        )
        FirebaseFirestore
            .getInstance()
            .collection("item_history")
            .document(uid)
            .set(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    pb.visibility = View.GONE
                    dialog.dismiss()
                    Toast.makeText(this, "Sukses mengupdate stok", Toast.LENGTH_SHORT).show()
                } else {
                    pb.visibility = View.GONE
                    dialog.dismiss()
                    Toast.makeText(this, "Gagal mengupdate stok, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showConfirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Obat")
            .setMessage("Apakah anda yakin ingin menghapus obat ini ?")
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
            .collection("common_herbs")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(this, "Sukses menghapus obat", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                } else {
                    Toast.makeText(this, "Gagal menghapus obat, terdapat kendala koneksi internet", Toast.LENGTH_SHORT).show()
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
                if(role == "admin" || role == "sales") {
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