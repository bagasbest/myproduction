package com.project.myproduction.ui.purchase_order

import android.annotation.SuppressLint
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

class POAdapter(private val poBtn: Button?) : RecyclerView.Adapter<POAdapter.ViewHolder>() {

    private val productList = ArrayList<POModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<POModel>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }


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
                    FirebaseFirestore
                        .getInstance()
                        .collection("purchase_order")
                        .document(model.uid!!)
                        .delete()
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                productList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                if(productList.size == 0) {
                                    poBtn?.isEnabled = false
                                }
                                Toast.makeText(itemView.context, "Berhasil menghapus obat", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(itemView.context, "Gagal menghapus obat, pastikan koneksi internet anda dalam keadaan stabil dan tidak terputus", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHerbsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}