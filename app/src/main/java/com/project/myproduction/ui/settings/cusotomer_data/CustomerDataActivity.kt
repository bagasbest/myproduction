package com.project.myproduction.ui.settings.cusotomer_data

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivityCustomerDataBinding
import com.project.myproduction.ui.settings.managing_sales.ManagingSalesAdapter
import com.project.myproduction.ui.settings.managing_sales.ManagingSalesViewModel

class CustomerDataActivity : AppCompatActivity() {

    private var binding: ActivityCustomerDataBinding? = null
    private var adapter: CustomerDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDataBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()
        initViewModel()

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding?.rvSales?.layoutManager = LinearLayoutManager(this)
        adapter = CustomerDataAdapter()
        binding?.rvSales?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[CustomerDataViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListCustomerData()
        viewModel.getCustomerData().observe(this) { customerDataList ->
            if (customerDataList.size > 0) {
                adapter?.setData(customerDataList)
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