package com.project.myproduction.ui.settings.managing_sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivityManagingSalesBinding

class ManagingSalesActivity : AppCompatActivity() {

    private var binding : ActivityManagingSalesBinding?= null
    private var adapter: ManagingSalesAdapter? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagingSalesBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }


    private fun initRecyclerView() {
        binding?.rvSales?.layoutManager = LinearLayoutManager(this)
        adapter = ManagingSalesAdapter()
        binding?.rvSales?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[ManagingSalesViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListSales()
        viewModel.getSales().observe(this) { salesList ->
            if (salesList.size > 0) {
                adapter?.setData(salesList)
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