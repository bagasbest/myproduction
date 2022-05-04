package com.project.myproduction.ui.surat_jalan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.myproduction.databinding.ActivitySuratJalanDetailBinding
import com.project.myproduction.ui.invoice.InvoiceModel

class SuratJalanDetailActivity : AppCompatActivity() {

    private var binding: ActivitySuratJalanDetailBinding? = null
    private var model: InvoiceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratJalanDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}