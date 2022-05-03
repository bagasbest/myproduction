package com.project.myproduction.ui.purchase_order.order

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityOrderDetailBinding
import com.project.myproduction.ui.obat_racikan.FormulatedModel
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private var model: OrderModel? = null
    private var adapter: OrderPOAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initRecyclerView()
        binding?.poId?.text = "PO ID: ${model?.uid}"
        binding?.customerName?.text = "Kepada Yth: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"
        binding?.salesName?.text = "Diajukan Oleh\n\n${model?.salesName}"
        binding?.tanggal?.text = "Tanggal: ${model?.date}"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.print?.setOnClickListener {

        }

        binding?.delete?.setOnClickListener {
            showAlertDeclineDialog()
        }
    }

    private fun showAlertDeclineDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menolak PO")
            .setMessage("Apakah anda yakin ingin menghapus PO ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deletePO()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun deletePO() {
        FirebaseFirestore
            .getInstance()
            .collection("order")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Berhasil menghapus PO", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal menghapus PO, koneksi internet anda bermasalah, coba lagi nanti", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initRecyclerView() {
        binding?.rvPo?.layoutManager = LinearLayoutManager(this)
        adapter = OrderPOAdapter()
        binding?.rvPo?.adapter = adapter
        adapter!!.setData(model?.product!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}