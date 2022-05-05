package com.project.myproduction.ui.purchase_order.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.myproduction.ui.obat_racikan.FormulatedModel
import com.project.myproduction.ui.purchase_order.POModel

class OrderViewModel : ViewModel() {

    private val orderList = MutableLiveData<ArrayList<OrderModel>>()
    private val listData = ArrayList<OrderModel>()
    private val TAG = OrderViewModel::class.java.simpleName


    fun setListOrder() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()

                        model.customer2ndAddress = document.data["customer2ndAddress"].toString()
                        model.uid = document.data["uid"].toString()
                        model.customer2ndName = document.data["customer2ndName"].toString()
                        model.customer2ndPhone = document.data["customer2ndPhone"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.dateInMillis = document.data["dateInMillis"] as Long
                        model.customerPhone = document.data["customerPhone"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.customerAddress = document.data["customerAddress"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.date = document.data["date"].toString()
                        model.category = document.data["category"].toString()
                        model.product = document.toObject(OrderModel::class.java).product

                        listData.add(model)
                    }
                    orderList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderByCommon() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("category", "common")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()

                        model.customer2ndAddress = document.data["customer2ndAddress"].toString()
                        model.uid = document.data["uid"].toString()
                        model.customer2ndName = document.data["customer2ndName"].toString()
                        model.customer2ndPhone = document.data["customer2ndPhone"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.dateInMillis = document.data["dateInMillis"] as Long
                        model.customerPhone = document.data["customerPhone"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.customerAddress = document.data["customerAddress"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.date = document.data["date"].toString()
                        model.category = document.data["category"].toString()
                        model.product = document.toObject(OrderModel::class.java).product

                        listData.add(model)
                    }
                    orderList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderByFormulated() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("category", "formulated")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()

                        model.customer2ndAddress = document.data["customer2ndAddress"].toString()
                        model.uid = document.data["uid"].toString()
                        model.customer2ndName = document.data["customer2ndName"].toString()
                        model.customer2ndPhone = document.data["customer2ndPhone"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.dateInMillis = document.data["dateInMillis"] as Long
                        model.customerPhone = document.data["customerPhone"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.customerAddress = document.data["customerAddress"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.date = document.data["date"].toString()
                        model.category = document.data["category"].toString()
                        model.product = document.toObject(OrderModel::class.java).product

                        listData.add(model)
                    }
                    orderList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getOrder() : LiveData<ArrayList<OrderModel>> {
        return orderList
    }

}