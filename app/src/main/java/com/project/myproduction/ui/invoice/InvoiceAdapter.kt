package com.project.myproduction.ui.invoice

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemInvoiceBinding
import com.project.myproduction.databinding.ItemTravelDocumentBinding
import com.project.myproduction.ui.surat_jalan.SuratJalanDetailActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InvoiceAdapter(private val option: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val invoiceList = ArrayList<InvoiceModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<InvoiceModel>) {
        invoiceList.clear()
        invoiceList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemInvoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: InvoiceModel, position: Int) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {

                if(position+1 < 10) {
                    poId.text = "Invoice ID: : ${model.dateInvoiceId}${String.format("%03d", position+1)}"
                } else if (position < 100) {
                    poId.text = "Invoice ID: : ${model.dateInvoiceId}${String.format("%02d", position+1)}"
                } else if (position < 1000) {
                    poId.text = "Invoice ID: : ${model.dateInvoiceId}${position+1}"
                }

                customerName.text = "Kepada Yth: ${model.customerName}"
                customerPhone.text = "No.Handphone: ${model.customerPhone}"
                customerAddress.text = "Alamat: ${model.customerAddress}"
                totalPrice.text = "Sub Total: Rp.${format.format(model.totalPrice)}"


                cv.setOnClickListener {
                    val intent = Intent(itemView.context, InvoiceDetailActivity::class.java)
                    intent.putExtra(InvoiceDetailActivity.EXTRA_DATA, model)
                    intent.putExtra(InvoiceDetailActivity.POSITION, position)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    inner class ViewHolder2(private val binding: ItemTravelDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: InvoiceModel) {
            with(binding) {

                customerName.text = "${model.customerName}"
                date.text = "${model.date}"
                address.text = model.customerAddress


                cv.setOnClickListener {
                    val intent = Intent(itemView.context, SuratJalanDetailActivity::class.java)
                    intent.putExtra(SuratJalanDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(option == "invoice") {
            val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding2 =
                ItemTravelDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder2(binding2)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(option == "invoice") {
            (holder as ViewHolder).bind(invoiceList[position], position)
        } else{
            (holder as ViewHolder2).bind(invoiceList[position])
        }
    }

    override fun getItemCount(): Int = invoiceList.size
}