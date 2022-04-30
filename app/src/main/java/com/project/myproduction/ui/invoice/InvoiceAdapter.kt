package com.project.myproduction.ui.invoice

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemInvoiceBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class InvoiceAdapter : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    private val invoiceList = ArrayList<InvoiceModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<InvoiceModel>) {
        invoiceList.clear()
        invoiceList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemInvoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: InvoiceModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {

                poId.text = "Invoice ID: : ${model.uid}"
                customerName.text = "Kepada Yth: ${model.customerName}"
                customerPhone.text = "No.Handphone: ${model.customerPhone}"
                customerAddress.text = "Alamat: ${model.customerAddress}"
                totalPrice.text = "Sub Total: Rp.${format.format(model.totalPrice)}"


                cv.setOnClickListener {
                    val intent = Intent(itemView.context, InvoiceDetailActivity::class.java)
                    intent.putExtra(InvoiceDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(invoiceList[position])
    }

    override fun getItemCount(): Int = invoiceList.size
}