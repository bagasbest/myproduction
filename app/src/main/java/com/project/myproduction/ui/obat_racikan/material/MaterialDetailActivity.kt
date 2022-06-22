package com.project.myproduction.ui.obat_racikan.material

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityMaterialDetailBinding
import com.project.myproduction.ui.obat_umum.HerbsDetailActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MaterialDetailActivity : AppCompatActivity() {

    private var binding: ActivityMaterialDetailBinding? = null
    private var model: MaterialModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        val formatter = DecimalFormat("#,###")


        model = intent.getParcelableExtra(HerbsDetailActivity.EXTRA_DATA)
        checkRole()
        binding?.name?.setText(model?.name)
        binding?.code?.setText(model?.code)
        binding?.type?.setText(model?.type)
        binding?.size?.setText(model?.size)
        binding?.price?.setText("Rp.${formatter.format(model?.price)}")
        Log.e("ta", model?.pricePerSize.toString())
        if(model?.pricePerSize != null) {
            binding?.pricePerSize?.setText("Rp.${formatter.format(model?.pricePerSize)}")
            binding?.stockSatuan?.setText(model?.stockPerSize.toString())
        } else {
            binding?.pricePerSize?.setText("Rp.0")
            binding?.stockSatuan?.setText("0")
        }
        binding?.stock?.setText(model?.stock.toString())
        binding?.stock?.setText(model?.stock.toString())

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, MaterialAddEditActivity::class.java)
            intent.putExtra(MaterialAddEditActivity.OPTION, "edit")
            intent.putExtra(MaterialAddEditActivity.EXTRA_DATA, model)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            showConfirmDelete()
        }

        binding?.addStock?.setOnClickListener {
            addStock()
        }

        binding?.stockTaking?.setOnClickListener {
            stockTaking()
        }
    }

    private fun stockTaking() {
        val currentStock = binding?.stock?.text.toString().toLong()
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
                if (currentStock > 0) {
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
                } else {
                    Toast.makeText(this, "Stok tidak mencukupi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun cutStock(stock: String, dialog: Dialog, pb: ProgressBar) {
        val resultStock = binding?.stock?.text.toString().toLong() - stock.toLong()
        val stockPerSize = resultStock * binding?.type?.text.toString().toLong()

        val data = mapOf(
            "stock" to resultStock,
            "stockPerSize" to stockPerSize,
        )

        FirebaseFirestore
            .getInstance()
            .collection("material")
            .document(model?.uid!!)
            .update(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    dialog.dismiss()
                    binding?.stock?.setText(resultStock.toString())
                    binding?.stockSatuan?.setText(stockPerSize.toString())
                    pb.visibility = View.GONE
                    Toast.makeText(this, "Sukses mengambil stok, log ini akan masuk item history", Toast.LENGTH_SHORT).show()
                } else {
                    dialog.dismiss()
                    pb.visibility = View.GONE
                    Toast.makeText(this, "Gagal mengambil stok, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun addStock() {
        val currentStock = binding?.stock?.text.toString().toLong()
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

                val stockPerSize = (currentStock+stock.toLong()) * binding?.type?.text.toString().toLong()

                val data = mapOf(
                    "stock" to currentStock+stock.toLong(),
                    "stockPerSize" to stockPerSize,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("material")
                    .document(model?.uid!!)
                    .update(data)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            binding?.stock?.setText("${currentStock+stock.toLong()}")
                            binding?.stockSatuan?.setText(stockPerSize.toString())
                            incomingStock(dialog, pb, stock)
                        } else {
                            pb.visibility = View.GONE
                            dialog.dismiss()
                            Toast.makeText(this, "Gagal mengupdate stok, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
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
            .collection("material")
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
                if(role == "admin" || role == "sales") {
                    binding?.edit?.visibility = View.VISIBLE
                    binding?.delete?.visibility = View.VISIBLE
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