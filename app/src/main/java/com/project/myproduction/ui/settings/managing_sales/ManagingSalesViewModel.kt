package com.project.myproduction.ui.settings.managing_sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ManagingSalesViewModel : ViewModel() {

    private val salesList = MutableLiveData<ArrayList<ManagingSalesModel>>()
    private val listData = ArrayList<ManagingSalesModel>()
    private val TAG = ManagingSalesViewModel::class.java.simpleName


    fun setListSales() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("role", "sales")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ManagingSalesModel()

                        model.email = document.data["email"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.username = document.data["username"].toString()
                        model.phone = document.data["phone"].toString()
                        model.work = document.data["work"].toString()
                        model.status = document.data["status"].toString()

                        listData.add(model)
                    }
                    salesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getSales() : LiveData<ArrayList<ManagingSalesModel>> {
        return salesList
    }


}