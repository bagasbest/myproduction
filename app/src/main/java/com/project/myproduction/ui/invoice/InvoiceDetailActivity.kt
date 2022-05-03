package com.project.myproduction.ui.invoice

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivityInvoiceDetailBinding
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter
import java.text.DecimalFormat
import java.text.NumberFormat

class InvoiceDetailActivity : AppCompatActivity() {

    private var binding: ActivityInvoiceDetailBinding? = null
    private var adapter: OrderPOAdapter? = null
    private var model: InvoiceModel? = null
    private var totalPrice = 0L

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initRecyclerView()
        binding?.date?.text = "Tanggal: ${model?.date}"
        binding?.customerName?.text = "Nama: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"
        binding?.invoiceId?.text = "INVOICE ID: INV-${model?.uid}"
        binding?.salesName?.text = "Nama Sales: ${model?.salesName}"

        if(model?.customer2ndName != "") {
            binding?.otherCustomer?.visibility = View.VISIBLE
            binding?.customerName2nd?.text = "Nama: ${model?.customer2ndName}"
            binding?.phone2nd?.text = "No.Handphone: ${model?.customer2ndPhone}"
            binding?.address2nd?.text = "Alamat: ${model?.customer2ndAddress}"
        }

        for(i in model?.product!!.indices) {
            totalPrice += model?.product!![i].price!!
        }
        val format: NumberFormat = DecimalFormat("#,###")
        binding?.totalPrice?.text = "Total Biaya: Rp.${format.format(totalPrice)}"

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