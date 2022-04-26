package com.project.myproduction.ui.purchase_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.project.myproduction.databinding.ActivityPoactivityBinding

class POActivity : AppCompatActivity() {

    private var binding: ActivityPoactivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.tabs?.addTab(binding?.tabs?.newTab()!!.setText("Obat Umum"))
        binding?.tabs?.addTab(binding?.tabs?.newTab()!!.setText("Obat Racikan"))

        val adapter = POPagerAdapter(supportFragmentManager, binding?.tabs!!.tabCount)

        binding?.viewPager?.adapter = adapter
        binding?.viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding?.tabs))
        binding?.tabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding?.viewPager?.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}