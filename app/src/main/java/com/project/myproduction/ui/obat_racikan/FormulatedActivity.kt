package com.project.myproduction.ui.obat_racikan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityFormulatedBinding
import com.project.myproduction.ui.obat_racikan.material.MaterialActivity
import com.project.myproduction.ui.obat_racikan.material.MaterialAddEditActivity

class FormulatedActivity : AppCompatActivity() {

    private var binding: ActivityFormulatedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulatedBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()
        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.addMaterial?.setOnClickListener {
            val intent = Intent(this, MaterialAddEditActivity::class.java)
            intent.putExtra(MaterialAddEditActivity.OPTION, "add")
            startActivity(intent)
        }

        binding?.material?.setOnClickListener {
            val intent = Intent(this, MaterialActivity::class.java)
            startActivity(intent)
        }

        binding?.addFormulated?.setOnClickListener {
            val intent = Intent(this, FormulatedAddEditActivity::class.java)
            intent.putExtra(FormulatedAddEditActivity.OPTION, "add")
            startActivity(intent)
        }

        binding?.formulatedList?.setOnClickListener {
            startActivity(Intent(this, FormulatedCatalogueActivity::class.java))
        }
    }

    private fun initView() {
        Glide.with(this)
            .load(R.drawable.obat_racikan)
            .into(binding!!.img1)

        Glide.with(this)
            .load(R.drawable.formula_add)
            .into(binding!!.img2)

        Glide.with(this)
            .load(R.drawable.material)
            .into(binding!!.img3)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}