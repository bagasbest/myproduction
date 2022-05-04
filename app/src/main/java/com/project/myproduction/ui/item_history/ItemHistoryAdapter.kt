package com.project.myproduction.ui.item_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemHistoryBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class ItemHistoryAdapter : RecyclerView.Adapter<ItemHistoryAdapter.ViewHolder>() {

    private val itemHistoryList = ArrayList<ItemHistoryModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<ItemHistoryModel>) {
        itemHistoryList.clear()
        itemHistoryList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: ItemHistoryModel) {
            val format: NumberFormat = DecimalFormat("#,###")
            with(binding) {


                textView23.text = "${model.status}, ID-${model.uid}"
                date.text = model.date
                when (model.status) {
                    "Incoming" -> {
                        stock.text = "+ ${model.stock}"
                        view6.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_red_light)
                    }
                    "Stock-taking" -> {
                        stock.text = "${model.stock}"
                        view6.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_orange_dark)
                    }
                    else -> {
                        stock.text = "- ${model.stock}"
                        view6.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_blue_dark)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemHistoryList[position])
    }

    override fun getItemCount(): Int = itemHistoryList.size
}