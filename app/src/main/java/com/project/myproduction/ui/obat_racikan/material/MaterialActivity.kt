package com.project.myproduction.ui.obat_racikan.material

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivityMaterialAddBinding
import com.project.myproduction.databinding.ActivityMaterialBinding
import com.project.myproduction.ui.obat_umum.HerbsAdapter
import com.project.myproduction.ui.obat_umum.HerbsViewModel

class MaterialActivity : AppCompatActivity() {

    private var binding: ActivityMaterialBinding? = null
    private var adapter: MaterialAdapter?= null
    private var from: String? = null
    private var to: String? = null


    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }


        binding?.search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(query: Editable?) {
                if(query.toString().isEmpty()) {
                    initRecyclerView()
                    initViewModel("")
                } else {
                    initRecyclerView()
                    initViewModel(query.toString())
                }
            }

        })

        binding?.sort?.setOnClickListener {
            sortProduct()
        }
    }

    private fun sortProduct() {
        val view: View = layoutInflater.inflate(R.layout.item_filter, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val upperPriceBtn = dialog.findViewById<Button>(R.id.upperPrice)
        val lowerPriceBtn = dialog.findViewById<Button>(R.id.lowerPrice)
        val upperStockBtn = dialog.findViewById<Button>(R.id.upperStock)
        val lowerStockBtn = dialog.findViewById<Button>(R.id.lowerStock)
        val priceRangeBtn = dialog.findViewById<Button>(R.id.rangePrice)
        val fromEt = dialog.findViewById<TextInputEditText>(R.id.from)
        val toEt = dialog.findViewById<TextInputEditText>(R.id.to)

        upperPriceBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("upperPrice")
            dialog.dismiss()
        }

        lowerPriceBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("lowerPrice")
            dialog.dismiss()
        }

        upperStockBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("upperStock")
            dialog.dismiss()
        }

        lowerStockBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("lowerStock")
            dialog.dismiss()
        }

        priceRangeBtn?.setOnClickListener {
            from = fromEt?.text.toString().trim()
            to = toEt?.text.toString().trim()

            if(from == null || from!!.toLong() < 0) {
                Toast.makeText(this, "Maaf, untuk filter harga tidak boleh kosong, dan lebih dari 0", Toast.LENGTH_SHORT).show()
            } else if (to == null || to?.toLong()!! < 0) {
                Toast.makeText(this, "Maaf, untuk filter harga tidak boleh kosong, dan lebih dari 0", Toast.LENGTH_SHORT).show()
            } else if (from?.toLong()!! > to?.toLong()!!) {
                Toast.makeText(this, "Maaf, untuk filter harga rentang adalah dari bilangan ter rendah hingga bilangan tertinggi, tidak boleh terbalik", Toast.LENGTH_SHORT).show()
            } else {
                initRecyclerView()
                initViewModel("priceRange")
                dialog.dismiss()
            }
        }


        dialog.show()
    }

    private fun initRecyclerView() {
        binding?.rvMaterial?.layoutManager = LinearLayoutManager(this)
        adapter = MaterialAdapter()
        binding?.rvMaterial?.adapter = adapter
    }

    private fun initViewModel(query: String) {
        val viewModel = ViewModelProvider(this)[MaterialViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        when (query) {
            "" -> {
                viewModel.setListMaterial()
            }
            "upperPrice" -> {
                viewModel.setListMaterialOrderByUpperStock()
            }
            "lowerPrice" -> {
                viewModel.setListMaterialOrderByLowerPrice()
            }
            "upperStock" -> {
                viewModel.setListMaterialOrderByUpperStock()
            }
            "lowerStock" -> {
                viewModel.setListMaterialOrderByLowerStock()
            }
            "priceRange" -> {
                viewModel.setListMaterialOrderByPriceRange(from?.toLong(), to?.toLong())
            }
            else -> {
                viewModel.setListMaterialOrderByQuery(query)
            }
        }
        viewModel.getMaterial().observe(this) { herbList ->
            if (herbList.size > 0) {
                adapter?.setData(herbList)
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