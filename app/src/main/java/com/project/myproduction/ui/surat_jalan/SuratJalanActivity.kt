package com.project.myproduction.ui.surat_jalan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.project.myproduction.databinding.ActivitySuratJalanBinding
import com.project.myproduction.ui.invoice.InvoiceAdapter
import com.project.myproduction.ui.invoice.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.*

class SuratJalanActivity : AppCompatActivity() {

    private var binding: ActivitySuratJalanBinding? = null
    private var adapter: InvoiceAdapter? = null
    private var from: Long? = 0L
    private var to: Long? = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratJalanBinding.inflate(layoutInflater)
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
                to = selection.toString().toLong() + 61200000
            }
        }

        binding?.filter?.setOnClickListener {
            if(from != 0L && to != 0L) {
                initRecyclerView()
                initViewModel("filter")
            } else {
                Toast.makeText(this, "Anda harus menginputkan tanggal awal - tanggal akhir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        binding?.rvTravelDocument?.layoutManager = LinearLayoutManager(this)
        adapter = InvoiceAdapter("travelDocument")
        binding?.rvTravelDocument?.adapter = adapter
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