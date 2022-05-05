package com.project.myproduction.ui.purchase_order.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private var binding: ActivityOrderBinding? = null
    private var adapter: OrderAdapter? = null
    private var category = ""

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel(category)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showDropdownCategory()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showDropdownCategory() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.category, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.category?.setAdapter(adapter)
        binding?.category?.setOnItemClickListener { _, _, _, _ ->
            category = binding?.category!!.text.toString()
            initRecyclerView()
            initViewModel(category)
        }
    }

    private fun initRecyclerView() {
        binding?.rvPo?.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter()
        binding?.rvPo?.adapter = adapter
    }

    private fun initViewModel(category: String) {
        val viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        if(category == "" || category == "Semua") {
            viewModel.setListOrder()
        } else if(category == "Obat Umum") {
            viewModel.setListOrderByCommon()

        } else if (category == "Obat Racikan") {
            viewModel.setListOrderByFormulated()

        }
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