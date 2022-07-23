package com.project.myproduction.ui.purchase_order.order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemPo2Binding
import com.project.myproduction.ui.obat_racikan.material.MaterialModel

class OrderPOAdapter2: RecyclerView.Adapter<OrderPOAdapter2.ViewHolder>() {

    private val productList = ArrayList<MaterialModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<MaterialModel>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemPo2Binding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: MaterialModel) {
            with(binding) {

                name.text = model.name
                code.text = model.code
                qtyType.text = "${model.qty} ${model.size}"

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPo2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}