package com.project.myproduction.ui.purchase_order

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.databinding.ItemHerbsBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class POAdapter(private val poList: ArrayList<POModel>, private val poBtn: Button?) : RecyclerView.Adapter<POAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemHerbsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: POModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                delete.visibility = View.VISIBLE
                name.text = model.name
                code.text = "Kode: ${model.code}"
                type.text = "Jenis: ${model.type}"
                price.text = "Harga: Rp.${format.format(model.price)}"
                stock.text = "Qty: ${model.qty}"

                delete.setOnClickListener {
                    val progressDialog = ProgressDialog(itemView.context)
                    progressDialog.setMessage("Silahkan tunggu hingga proses selesai...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    FirebaseFirestore
                        .getInstance()
                        .collection("purchase_order")
                        .document(model.uid!!)
                        .delete()
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                poList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                if(poList.size == 0) {
                                    poBtn?.isEnabled = false
                                }
                                /// cutStock
                                cutStock(model, progressDialog, itemView.context)
                                Toast.makeText(itemView.context, "Berhasil menghapus obat", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(itemView.context, "Gagal menghapus obat, pastikan koneksi internet anda dalam keadaan stabil dan tidak terputus", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

    }

    private fun cutStock(model: POModel, progressDialog: ProgressDialog, context: Context) {
        if(model.category == "common") {
            FirebaseFirestore
                .getInstance()
                .collection("common_herbs")
                .document(model.productId!!)
                .get()
                .addOnSuccessListener {
                    val currentStock = it.data!!["stock"] as Long

                    FirebaseFirestore
                        .getInstance()
                        .collection("common_herbs")
                        .document(model.productId!!)
                        .update("stock", currentStock+model.qty!!)
                        .addOnCompleteListener { task->
                            if(task.isSuccessful) {
                                progressDialog.dismiss()
                                Toast.makeText(context, "Berhasil menghapus produk", Toast.LENGTH_SHORT).show()
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(context, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
        } else {

            for(index in model.formulatedQty!!.indices) {
                val formulatedQty = model.formulatedQty!![index]
                val result = formulatedQty * model.qty!!

                val collection = model.material!![index].collection!!

                FirebaseFirestore
                    .getInstance()
                    .collection(collection)
                    .document(model.materialId!![index])
                    .get()
                    .addOnSuccessListener {
                        val currentStock = it.data!!["stock"] as Long

                        FirebaseFirestore
                            .getInstance()
                            .collection(collection)
                            .document(model.materialId!![index])
                            .update("stock", currentStock + result)
                    }
            }

            val prefs = context.getSharedPreferences(
                "formulated", Context.MODE_PRIVATE
            )
            prefs?.edit()?.putBoolean("isAdd", false)?.apply()

            Handler().postDelayed({
                progressDialog.dismiss()
                Toast.makeText(context, "Berhasil menghapus produk", Toast.LENGTH_SHORT).show()
            }, 3000)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHerbsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(poList[position])
    }

    override fun getItemCount(): Int = poList.size
}