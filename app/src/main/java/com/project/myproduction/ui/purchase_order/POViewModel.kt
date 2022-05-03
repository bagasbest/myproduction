package com.project.myproduction.ui.purchase_order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class POViewModel : ViewModel() {

    private val productList = MutableLiveData<ArrayList<POModel>>()
    private val listData = ArrayList<POModel>()
    private val TAG = POViewModel::class.java.simpleName


    fun setListProductCommon(salesId: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("purchase_order")
                .whereEqualTo("salesId", salesId)
                .whereEqualTo("category", "common")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = POModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.qty = document.data["qty"] as Long
                        model.type = document.data["type"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.salesId = document.data["salesId"].toString()
                        model.productId = document.data["productId"].toString()
                        model.materialId = document.data["materialId"] as ArrayList<String>
                        model.category = document.data["category"].toString()

                        listData.add(model)
                    }
                    productList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListProductFormulated(salesId: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("purchase_order")
                .whereEqualTo("salesId", salesId)
                .whereEqualTo("category", "formulated")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = POModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.qty = document.data["qty"] as Long
                        model.type = document.data["type"].toString()
                        model.salesName = document.data["salesName"].toString()
                        model.salesId = document.data["salesId"].toString()
                        model.productId = document.data["productId"].toString()
                        model.materialId = document.data["materialId"] as ArrayList<String>
                        model.category = document.data["category"].toString()
                        model.formulatedQty = document.data["formulatedQty"] as ArrayList<Long>

                        listData.add(model)
                    }
                    productList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getProduct() : LiveData<ArrayList<POModel>> {
        return productList
    }
}