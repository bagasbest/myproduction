package com.project.myproduction.ui.obat_racikan

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.R
import com.project.myproduction.databinding.ItemHerbsBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class FormulatedAdapter : RecyclerView.Adapter<FormulatedAdapter.ViewHolder>() {

    private val formulatedList = ArrayList<FormulatedModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<FormulatedModel>) {
        formulatedList.clear()
        formulatedList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemHerbsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: FormulatedModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {

                name.text = model.name
                code.text = "Kode: ${model.code}"
                type.text = "Jenis: ${model.type}"
                price.text = "Harga: Rp.${format.format(model.price)}"
                stock.text = "Stok: ${model.stock}"

                if (model.stock!! > 0) {
                    linearLayout.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.blue)
                } else {
                    linearLayout.backgroundTintList = ContextCompat.getColorStateList(
                        itemView.context,
                        android.R.color.holo_red_light
                    )
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, FormulatedDetailActivity::class.java)
                    intent.putExtra(FormulatedDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHerbsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(formulatedList[position])
    }

    override fun getItemCount(): Int = formulatedList.size
}