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
    private var date: String? = null
    private var dateInMillis: Long = 0L

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
        binding?.status?.text = "Status: ${model?.status}"
        binding?.salesName?.text = "Diajukan Oleh\n\n${model?.salesName}"
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MMMM-yyyy, HH:mm", Locale.getDefault())
        date = sdf.format(calendar.time)
        dateInMillis = calendar.get(Calendar.MILLISECOND).toLong()
        binding?.tanggal?.text = "Tanggal: $date"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.print?.setOnClickListener {

        }

        binding?.accBtn?.setOnClickListener {
            showAlertAccDialog()
        }

        binding?.decline?.setOnClickListener {
            showAlertDeclineDialog()
        }
    }

    private fun showAlertAccDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Meneima PO")
            .setMessage("Apakah anda yakin ingin menerima PO yang telah di ajukan sales ?\n\nJika Ya, maka stok obat akan terpotong, dan invoice dati PO ini akan segera terbit, ingin melanjutkan ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                accPO()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun accPO() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Silahkan tunggu hingga proses selesai...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // update status
        FirebaseFirestore
            .getInstance()
            .collection("order")
            .document(model?.uid!!)
            .update("status", "Sudah Disetujui")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding?.status?.text = "Sudah Disetujui"
                    /// potong stok
                    for (i in model?.product?.indices!!) {
                        /// potong common product
                        if (model?.product!![i].category == "common") {
                            val materialId = model?.product!![i].materialId!![0]
                            val qty = model?.product!![i].qty

                            FirebaseFirestore
                                .getInstance()
                                .collection("common_herbs")
                                .document(materialId)
                                .get()
                                .addOnSuccessListener { task ->
                                    val stockProduct = task.data!!["stock"] as Long
                                    if (stockProduct - qty!! >= 0) {

                                        /// update new stock
                                        FirebaseFirestore
                                            .getInstance()
                                            .collection("common_herbs")
                                            .document(materialId)
                                            .update("stock", stockProduct - qty)
                                            .addOnCompleteListener { result ->
                                                if (result.isSuccessful) {
                                                    createInvoice(progressDialog)
                                                } else {
                                                    progressDialog.dismiss()
                                                    showFailureDialog()
                                                }
                                            }

                                    } else {
                                        progressDialog.dismiss()
                                        showFailureDialog()
                                    }
                                }
                        } else {

                        }
                    }
                } else {
                    progressDialog.dismiss()
                    showFailureDialog()
                }
            }
    }

    private fun createInvoice(progressDialog: ProgressDialog) {
        /// buat invoice
        val invoiceId =
            System.currentTimeMillis().toString()
        val invoice = mapOf(
            "uid" to invoiceId,
            "date" to date,
            "dateInMillis" to dateInMillis,
            "salesName" to model?.salesName,
            "customerName" to model?.customerName,
            "customerPhone" to model?.customerPhone,
            "customerAddress" to model?.customerAddress,
            "customer2ndName" to model?.customer2ndName,
            "customer2ndPhone" to model?.customer2ndPhone,
            "customer2ndAddress" to model?.customer2ndAddress,
            "product" to model?.product,
            "totalPrice" to model?.totalPrice,
        )

        FirebaseFirestore
            .getInstance()
            .collection("invoice")
            .document(invoiceId)
            .set(invoice)
            .addOnCompleteListener { inv ->
                if (inv.isSuccessful) {
                    progressDialog.dismiss()
                    showSuccessDialog()
                } else {
                    progressDialog.dismiss()
                    showFailureDialog()
                }
            }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menyetujui PO")
            .setMessage("Ups, koneksi internet anda bermasalah, silahkan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menyetujui PO")
            .setMessage("Stok obat telah di potong, invoice untuk PO ini sudah muncul, silahkan cek menu Invoice\n\nAnda juga bisa mencetak PO ini dan menyerahkan ke bagian obat untuk di persiapkan dan di packing.")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    private fun showAlertDeclineDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menolak PO")
            .setMessage("Apakah anda yakin ingin menolak PO yang telah di ajukan sales ?\n\nJika ditolak, otomatis PO ini akan segera di hapus, ingin melanjutkan ?")
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
                    Toast.makeText(this, "Berhasil menolak PO", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal menolak PO", Toast.LENGTH_SHORT).show()
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