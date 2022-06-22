package com.project.myproduction.ui.item_history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityItemHistoryBinding
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ItemHistoryActivity : AppCompatActivity() {

    private var binding: ActivityItemHistoryBinding? = null
    private var adapter: ItemHistoryAdapter? = null
    private var productNameValue = ""
    private var customerDataValue = ""

    private var from = 0L
    private var to = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()
        initViewModel("all")

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.filterBtn?.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val view: View = layoutInflater.inflate(R.layout.popup_item_history, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val dateStartBtn = dialog.findViewById<Button>(R.id.dateStart)
        val dateFinishBtn = dialog.findViewById<Button>(R.id.dateFinish)
        val searchDateBtn = dialog.findViewById<ImageButton>(R.id.searchDateBtn)

        val productNameSpinner = dialog.findViewById<SearchableSpinner>(R.id.productNameSpinner)
        val productNameBtn = dialog.findViewById<Button>(R.id.productNameBtn)

        val spinner = dialog.findViewById<SearchableSpinner>(R.id.searchable_spinner)
        val spinnerBtn = dialog.findViewById<Button>(R.id.customerDataBtn)

        /// spinner value
        val viewModel = ViewModelProvider(this)[ItemHistoryViewModel::class.java]
        viewModel.setItemHistory()
        viewModel.getItemHistory().observe(this) { listData ->
            if (listData.size > 0) {

                val customerName = getItemHistoryList(listData, "customerName")
                val productName = getItemHistoryList(listData, "productName")

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_expandable_list_item_1,
                    productName
                )
                val adapter2 = ArrayAdapter(
                    this,
                    android.R.layout.simple_expandable_list_item_1,
                    customerName
                )

                productNameSpinner?.adapter = adapter
                spinner?.adapter = adapter2


                productNameSpinner?.onItemSelectedListener =
                    (object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                             productNameValue = listData[p2].productName.toString()
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    })
                spinner?.onItemSelectedListener =
                    (object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            customerDataValue = listData[p2].customerName.toString()
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    })
            }
        }

        /// filter by customer name btn
        spinnerBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("customerName")

            dialog.dismiss()
        }

        /// filter by product name
        productNameBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("productName")

            dialog.dismiss()
        }

        dateStartBtn?.setOnClickListener {
            val datePicker: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setTitleText("Filter Item History Dari Tanggal").build()
            datePicker.show(supportFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener { selection: Any ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val format = sdf.format(Date(selection.toString().toLong()))
                dateStartBtn.text = format
                from = selection.toString().toLong() - 25200000
            }
        }

        dateFinishBtn?.setOnClickListener {
            val datePicker: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setTitleText("Filter Item History Ke Tanggal").build()
            datePicker.show(supportFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener { selection: Any ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val format = sdf.format(Date(selection.toString().toLong()))
                dateFinishBtn.text = format
                to = selection.toString().toLong() + 61200000
            }
        }

        searchDateBtn?.setOnClickListener {
            if(from != 0L && to != 0L) {
                initRecyclerView()
                initViewModel("date")
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Anda harus menginputkan tanggal awal - tanggal akhir", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun getItemHistoryList(
        itemHistoryList: ArrayList<ItemHistoryModel>,
        option: String
    ): List<String> {
        val result = mutableSetOf<String>()
        for (material in itemHistoryList) {
            if (option == "productName") {
                result.add(material.productName!!)
            } else {
                result.add(material.customerName!!)
            }
        }

        return result.toList()
    }


    private fun initRecyclerView() {
        binding?.rvItemHistory?.layoutManager = LinearLayoutManager(this)
        adapter = ItemHistoryAdapter()
        binding?.rvItemHistory?.adapter = adapter
    }

    private fun initViewModel(option: String) {
        val viewModel = ViewModelProvider(this)[ItemHistoryViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        when (option) {
            "all" -> {
                viewModel.setItemHistory()
            }
            "productName" -> {
                viewModel.setItemHistoryByQuery(productNameValue, "productName")
            }
            "customerName" -> {
                viewModel.setItemHistoryByQuery(customerDataValue, "customerName")
            }
            "date"-> {
                viewModel.setItemHistoryByDate(from, to)
            }
        }
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