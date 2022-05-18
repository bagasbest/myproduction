package com.project.myproduction.ui.settings.cusotomer_data

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.ItemUserDataBinding
import kotlin.collections.ArrayList

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

                cv.setOnClickListener {
                    showPopupEditCustomerData(model, itemView.context, name, phone, address)
                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showPopupEditCustomerData(
        model: CustomerDataModel,
        context: Context,
        nameTv: TextView,
        phoneTv: TextView,
        addressTv: TextView
    ) {
        val nameEt: TextInputEditText
        val phoneEt: TextInputEditText
        val addressEt: TextInputEditText
        val confirmBtn: Button
        val dismissBtn: Button
        val pb: ProgressBar
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_edit_customer_data)
        nameEt = dialog.findViewById(R.id.name)
        phoneEt = dialog.findViewById(R.id.phone)
        addressEt = dialog.findViewById(R.id.address)
        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        dismissBtn = dialog.findViewById(R.id.dismiss)
        pb = dialog.findViewById(R.id.progressBar)

        nameEt.setText(model.name)
        phoneEt.setText(model.phone)
        addressEt.setText(model.address)

        confirmBtn?.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val phone = phoneEt.text.toString().trim()
            val address = addressEt.text.toString().trim()

            if(name.isEmpty()) {
                Toast.makeText(context, "Maaf, nama kustomer tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (phone.isEmpty()) {
                Toast.makeText(context, "Maaf, No.Handphone kustomer tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (address.isEmpty()) {
                Toast.makeText(context, "Maaf, Alamat kustomer tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }

            else {
                pb.visibility = View.VISIBLE

                val data = mapOf(
                    "address" to address,
                    "name" to name,
                    "phone" to phone,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("customer_data")
                    .document(model.uid!!)
                    .update(data)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            nameTv.text = name
                            phoneTv.text = "No.Handphone: ${model.phone}"
                            addressTv.text = "Alamat: ${model.address}"
                            Toast.makeText(context, "Berhasil memperbarui Customer Data!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            pb.visibility = View.GONE
                            Toast.makeText(context, "Gagal memperbarui Customer Data!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
            }
        }

        dismissBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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