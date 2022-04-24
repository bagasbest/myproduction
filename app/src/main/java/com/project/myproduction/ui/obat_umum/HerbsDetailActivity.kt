package com.project.myproduction.ui.obat_umum

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

class HerbsDetailActivity : AppCompatActivity() {

    private var binding: ActivityHerbsDetailBinding? = null
    private var model : HerbsModel? = null

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
                                dialog.dismiss()
                                binding?.stock?.setText("${currentStock+stock.toLong()}")
                                Toast.makeText(this, "Sukses mengupdate stok", Toast.LENGTH_SHORT).show()
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