package com.project.myproduction.ui.purchase_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.myproduction.databinding.ActivityPodetailBinding

class PODetailActivity : AppCompatActivity() {

    private var binding: ActivityPodetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}