package com.project.myproduction.ui.purchase_order.order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemPoBinding
import com.project.myproduction.ui.purchase_order.POModel
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderPOAdapter: RecyclerView.Adapter<OrderPOAdapter.ViewHolder>() {

    private val productList = ArrayList<POModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<POModel>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemPoBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: POModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {

                name.text = model.name
                qty.text = "${model.qty} / ${model.type} ${model.size}"
                totalPrice.text = "Rp.${format.format(model.price)}"

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}