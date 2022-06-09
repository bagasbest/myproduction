package com.project.myproduction.ui.item_history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.myproduction.databinding.ActivityItemHistoryDetailBinding

class ItemHistoryDetailActivity : AppCompatActivity() {

    private var binding : ActivityItemHistoryDetailBinding ? = null
    private var model : ItemHistoryModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        binding?.name?.text = "Nama produk : ${model?.productName}"
        binding?.date?.text = "Tanggal : ${model?.date}"
        binding?.status?.text = "Status : ${model?.status}"
        binding?.code?.text = "Kode produk : ${model?.productName}"
        binding?.type?.text = "Tipe : ${model?.productName}"
        binding?.qty?.text = "Kuantitas : ${model?.stock}"
        binding?.customerName?.text = "Nama kustomer : ${model?.customerName}"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
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