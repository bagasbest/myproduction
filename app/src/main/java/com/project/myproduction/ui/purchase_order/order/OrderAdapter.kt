package com.project.myproduction.ui.purchase_order.order

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ItemHerbsBinding
import com.project.myproduction.databinding.ItemOrderBinding
import com.project.myproduction.ui.purchase_order.POModel
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val orderList = ArrayList<OrderModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<OrderModel>) {
        orderList.clear()
        orderList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: OrderModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {

                poId.text = "PO ID: ${model.uid}"
                customerName.text = "Kepada Yth: ${model.customerName}"
                customerAddress.text = "Alamat: ${model.customerAddress}"
                totalPrice.text = "Sub Total: Rp.${format.format(model.totalPrice)}"

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, OrderDetailActivity::class.java)
                    intent.putExtra(OrderDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size
}