package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemMaterialBinding
import com.project.myproduction.ui.obat_racikan.material.MaterialModel

class FormulatedMaterialAdapter(
    private val option: String,
    private val materialList: ArrayList<MaterialModel>?
) : RecyclerView.Adapter<FormulatedMaterialAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: MaterialModel, position: Int) {
            with(binding) {

                name.text = model.name
                code.text = "Kode: ${model.code}"
                type.text = "Jenis: ${model.type}"

                when (option) {
                    "add" -> {
                        delete.visibility = View.VISIBLE
                        qtyEt.isEnabled = true
                        qty.addTextChangedListener(object: TextWatcher{
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {

                            }

                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                            }

                            override fun afterTextChanged(query: Editable?) {
                                if(query.toString().isEmpty() || query.toString().toInt() <= 0) {
                                    Toast.makeText(itemView.context, "Kuantitas bahan baku ''${model.name}'' tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                    return
                                } else {
                                    Log.e("taf", materialList!![position].name!!)
                                    Log.e("position", position.toString())
                                    materialList!![position].qty = query.toString().toLong()
                                }
                            }
                        })
                    }
                    "edit" -> {
                        delete.visibility = View.VISIBLE
                        qty.isEnabled = true
                        qty.setText(model.qty.toString())

                        qty.addTextChangedListener(object: TextWatcher{
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {

                            }

                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                            }

                            override fun afterTextChanged(query: Editable?) {
                                if(query.toString().isEmpty() || query.toString().toInt() <= 0) {
                                    Toast.makeText(itemView.context, "Kuantitas bahan baku ''${model.name}'' tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                    return
                                } else {
                                    Log.e("taf", materialList!![position].name!!)
                                    Log.e("position", position.toString())
                                    materialList!![position].qty = query.toString().toLong()
                                }
                            }
                        })
                    }
                    "detail" -> {
                        qty.setText(model.qty.toString())
                    }
                }
                delete.setOnClickListener {
                    materialList?.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(materialList!![position], position)
    }

    override fun getItemCount(): Int = materialList?.size!!
}