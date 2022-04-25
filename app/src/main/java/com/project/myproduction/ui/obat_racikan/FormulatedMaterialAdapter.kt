package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        fun bind(model: MaterialModel) {
            with(binding) {

                name.text = model.name
                code.text = "Kode: ${model.code}"
                type.text = "Jenis: ${model.type}"

                if (option == "add") {
                    delete.visibility = View.VISIBLE
                    qtyEt.isEnabled = true
                    val qty = qty.text.toString().trim()
                    materialList?.get(adapterPosition)!!.qty = qty.toLong()
                } else if (option == "edit") {
                    delete.visibility = View.VISIBLE
                    qty.setText(model.qty.toString())
                }

                delete.setOnClickListener {
                    materialList?.removeAt(adapterPosition)
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
        holder.bind(materialList!![position])
    }

    override fun getItemCount(): Int = materialList?.size!!
}