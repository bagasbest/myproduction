package com.project.myproduction.ui.purchase_order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemHerbsBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class POAdapter : RecyclerView.Adapter<POAdapter.ViewHolder>() {

    private val productList = ArrayList<POModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<POModel>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemHerbsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: POModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {

                name.text = model.name
                code.text = "Kode: ${model.code}"
                type.text = "Jenis: ${model.type}"
                price.text = "Harga: Rp.${format.format(model.price)}"
                stock.text = "Qty: ${model.qty}"

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