package com.project.myproduction.ui.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.myproduction.databinding.ActivityInvoiceDetailBinding

class InvoiceDetailActivity : AppCompatActivity() {

    private var binding: ActivityInvoiceDetailBinding? = null
    private var model: InvoiceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)

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