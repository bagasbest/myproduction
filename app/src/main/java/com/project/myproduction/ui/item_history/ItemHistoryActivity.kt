package com.project.myproduction.ui.item_history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproduction.databinding.ActivityItemHistoryBinding

class ItemHistoryActivity : AppCompatActivity() {

    private var binding: ActivityItemHistoryBinding? = null
    private var adapter: ItemHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()
        initViewModel()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }


    private fun initRecyclerView() {
        binding?.rvItemHistory?.layoutManager = LinearLayoutManager(this)
        adapter = ItemHistoryAdapter()
        binding?.rvItemHistory?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[ItemHistoryViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setItemHistory()
        viewModel.getItemHistory().observe(this) { itemHistoryList ->
            if (itemHistoryList.size > 0) {
                adapter?.setData(itemHistoryList)
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