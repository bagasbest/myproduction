package com.project.myproduction.ui.invoice

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class InvoiceViewModel : ViewModel() {

    private val invoiceList = MutableLiveData<ArrayList<InvoiceModel>>()
    private val listData = ArrayList<InvoiceModel>()
    private val TAG = InvoiceViewModel::class.java.simpleName


    fun setListInvoice() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("invoice")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = InvoiceModel()

                        model.customer2ndAddress = document.data["customer2ndAddress"].toString()
                        model.uid = document.data["uid"].toString()
                        model.customer2ndName = document.data["customer2ndName"].toString()
                        model.customer2ndPhone = document.data["customer2ndPhone"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.customerPhone = document.data["customerPhone"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.customerAddress = document.data["customerAddress"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.date = document.data["date"].toString()
                        model.dateInMillis = document.data["date"] as Long
                        model.product = document.toObject(InvoiceModel::class.java).product

                        listData.add(model)
                    }
                    invoiceList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListInvoiceByDate(from: Long, to: Long) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("invoice")
                .whereGreaterThanOrEqualTo("dateInMillis", from)
                .whereLessThanOrEqualTo("dateInMillis", to)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = InvoiceModel()

                        model.customer2ndAddress = document.data["customer2ndAddress"].toString()
                        model.uid = document.data["uid"].toString()
                        model.customer2ndName = document.data["customer2ndName"].toString()
                        model.customer2ndPhone = document.data["customer2ndPhone"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.customerPhone = document.data["customerPhone"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.customerAddress = document.data["customerAddress"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.date = document.data["date"].toString()
                        model.dateInMillis = document.data["dateInMillis"] as Long
                        model.product = document.toObject(InvoiceModel::class.java).product

                        listData.add(model)
                    }
                    invoiceList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getInvoice() : LiveData<ArrayList<InvoiceModel>> {
        return invoiceList
    }
}