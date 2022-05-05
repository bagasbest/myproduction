package com.project.myproduction.ui.surat_jalan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivitySuratJalanDetailBinding
import com.project.myproduction.ui.invoice.InvoiceDetailActivity
import com.project.myproduction.ui.invoice.InvoiceModel
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter2
import java.text.DecimalFormat
import java.text.NumberFormat

class SuratJalanDetailActivity : AppCompatActivity() {

    private var binding: ActivitySuratJalanDetailBinding? = null
    private var adapter: OrderPOAdapter? = null
    private var adapter2: OrderPOAdapter2? = null
    private var model: InvoiceModel? = null
    private var totalPrice = 0L

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratJalanDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(InvoiceDetailActivity.EXTRA_DATA)
        initRecyclerView()
        binding?.date?.text = "Tanggal: ${model?.date}"
        binding?.customerName?.text = "Nama: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"

        if(model?.category == "common") {
            binding?.rvCommon?.visibility = View.VISIBLE
            binding?.common1?.visibility = View.VISIBLE
        } else {
            binding?.rvFormulated?.visibility = View.VISIBLE
            binding?.komposisi?.visibility = View.VISIBLE
            binding?.productName?.visibility = View.VISIBLE
            binding?.qtyFormulated?.visibility = View.VISIBLE
            binding?.productName?.text = "Nama Produk Racikan: ${model?.product!![0].name}"
            binding?.qtyFormulated?.text = "Kuantitas Pemesanan: ${model?.product!![0].qty}"
        }

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
        if(model?.category == "common") {
            binding?.rvCommon?.layoutManager = LinearLayoutManager(this)
            adapter = OrderPOAdapter()
            binding?.rvCommon?.adapter = adapter
            adapter!!.setData(model?.product!!)
        } else {
            binding?.rvFormulated?.layoutManager = LinearLayoutManager(this)
            adapter2 = OrderPOAdapter2()
            binding?.rvFormulated?.adapter = adapter2
            adapter2!!.setData(model?.product!![0].material!!)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}