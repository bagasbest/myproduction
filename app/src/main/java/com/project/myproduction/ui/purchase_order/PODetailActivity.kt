package com.project.myproduction.ui.purchase_order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.myproduction.databinding.ActivityPodetailBinding
import com.project.myproduction.ui.purchase_order.order.OrderActivity

class PODetailActivity : AppCompatActivity() {

    private var binding: ActivityPodetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.poList?.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }


        binding?.createPO?.setOnClickListener {
            startActivity(Intent(this, POActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}