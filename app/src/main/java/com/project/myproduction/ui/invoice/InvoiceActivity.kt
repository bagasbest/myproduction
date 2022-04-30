package com.project.myproduction.ui.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.project.myproduction.databinding.ActivityInvoiceBinding
import java.text.SimpleDateFormat
import java.util.*

class InvoiceActivity : AppCompatActivity() {

    private var binding: ActivityInvoiceBinding? = null
    private var adapter: InvoiceAdapter? = null
    private var from: Long? = 0L
    private var to: Long? = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        initRecyclerView()
        initViewModel("all")

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.from?.setOnClickListener {
            val datePicker: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setTitleText("Filter Invoice Dari Tanggal").build()
            datePicker.show(supportFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener { selection: Any ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val format = sdf.format(Date(selection.toString().toLong()))
                binding?.from?.text = format
                from = selection.toString().toLong()
            }
        }


        binding?.to?.setOnClickListener {
            val datePicker: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setTitleText("Filter Invoice Ke Tanggal").build()
            datePicker.show(supportFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener { selection: Any ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val format = sdf.format(Date(selection.toString().toLong()))
                binding?.to?.text = format
                to = selection.toString().toLong()
            }
        }

        binding?.filter?.setOnClickListener {
            if(from != null && to != null) {
                initRecyclerView()
                initViewModel("filter")
            }
        }
    }

    private fun initRecyclerView() {
        binding?.rvInvoice?.layoutManager = LinearLayoutManager(this)
        adapter = InvoiceAdapter()
        binding?.rvInvoice?.adapter = adapter
    }

    private fun initViewModel(option: String) {
        val viewModel = ViewModelProvider(this)[InvoiceViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        if (option == "all") {
            viewModel.setListInvoice()
        } else {
            viewModel.setListInvoiceByDate(from!!, to!!)
        }
        viewModel.getInvoice().observe(this) { order ->
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