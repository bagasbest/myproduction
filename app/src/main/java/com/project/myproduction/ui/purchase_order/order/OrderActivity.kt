package com.project.myproduction.ui.purchase_order.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivityOrderBinding
import com.project.myproduction.ui.settings.managing_sales.ManagingSalesAdapter
import com.project.myproduction.ui.settings.managing_sales.ManagingSalesViewModel

class OrderActivity : AppCompatActivity() {

    private var binding: ActivityOrderBinding? = null
    private var adapter: OrderAdapter? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initRecyclerView() {
        binding?.rvPo?.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter()
        binding?.rvPo?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListOrder()
        viewModel.getOrder().observe(this) { order ->
            if (order.size > 0) {
                adapter?.setData(order)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}