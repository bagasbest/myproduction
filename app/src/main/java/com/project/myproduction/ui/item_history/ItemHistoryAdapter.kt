package com.project.myproduction.ui.item_history

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
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
            with(binding) {


                textView23.text = "${model.status}, ID-${model.uid}"
                date.text = model.date
                when (model.status) {
                    "Incoming" -> {
                        customerName.visibility = View.GONE
                        stock.text = "+ ${model.stock}"
                        view6.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_red_light)
                    }
                    "Stock-taking" -> {
                        customerName.visibility = View.GONE
                        stock.text = "${model.stock}"
                        view6.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_orange_dark)
                    }
                    else -> {
                        customerName.visibility = View.VISIBLE
                        customerName.text = model.customerName
                        stock.text = "- ${model.stock}"
                        view6.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_blue_dark)
                    }
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, ItemHistoryDetailActivity::class.java)
                    intent.putExtra(ItemHistoryDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
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