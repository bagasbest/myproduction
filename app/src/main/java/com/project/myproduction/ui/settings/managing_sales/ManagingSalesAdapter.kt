package com.project.myproduction.ui.settings.managing_sales

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.R
import com.project.myproduction.databinding.ItemSalesBinding

class ManagingSalesAdapter : RecyclerView.Adapter<ManagingSalesAdapter.ViewHolder>() {

    private val salesList = ArrayList<ManagingSalesModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<ManagingSalesModel>) {
        salesList.clear()
        salesList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemSalesBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: ManagingSalesModel) {
            with(binding) {

                name.text = model.name
                email.text = "Email: ${model.email}"
                work.text = "Pekerjaan: ${model.work}"
                username.text = "Username: ${model.username}"
                status.text = model.status

                if(model.status == "Aktif") {
                    linearLayout.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.blue)
                } else {
                    linearLayout.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_red_light)
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, ManagingSalesDetailActivity::class.java)
                    intent.putExtra(ManagingSalesDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(salesList[position])
    }

    override fun getItemCount(): Int = salesList.size
}