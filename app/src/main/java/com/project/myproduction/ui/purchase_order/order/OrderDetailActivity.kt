package com.project.myproduction.ui.purchase_order.order

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivityOrderDetailBinding
import com.project.myproduction.ui.settings.cusotomer_data.CustomerDataAdapter

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private var model : OrderModel? = null
    private var adapter: OrderPOAdapter? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initRecyclerView()
        binding?.poId?.text = "PO ID: ${model?.uid}"
        binding?.customerName?.text = "Kepada Yth: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"
        binding?.status?.text = "Status: ${model?.status}"
        binding?.salesName?.text = "Diajukan Oleh\n\n${model?.salesName}"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.print?.setOnClickListener {

        }

        binding?.accBtn?.setOnClickListener {

        }

        binding?.decline?.setOnClickListener {

        }
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