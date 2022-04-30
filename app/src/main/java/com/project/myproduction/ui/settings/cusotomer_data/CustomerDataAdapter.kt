package com.project.myproduction.ui.settings.cusotomer_data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproduction.databinding.ItemUserDataBinding

class CustomerDataAdapter : RecyclerView.Adapter<CustomerDataAdapter.ViewHolder>() {

    private val customerDataList = ArrayList<CustomerDataModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CustomerDataModel>) {
        customerDataList.clear()
        customerDataList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemUserDataBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: CustomerDataModel) {
            with(binding) {

                name.text = model.name
                phone.text = "No.Handphone: ${model.phone}"
                address.text = "Alamat: ${model.address}"

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customerDataList[position])
    }

    override fun getItemCount(): Int = customerDataList.size
}