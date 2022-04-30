package com.project.myproduction.ui.settings.cusotomer_data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CustomerDataViewModel : ViewModel() {

    private val customerList = MutableLiveData<ArrayList<CustomerDataModel>>()
    private val listData = ArrayList<CustomerDataModel>()
    private val TAG = CustomerDataViewModel::class.java.simpleName


    fun setListCustomerData() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("customer_data")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = CustomerDataModel()

                        model.address = document.data["address"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.phone = document.data["phone"].toString()


                        listData.add(model)
                    }
                    customerList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getCustomerData() : LiveData<ArrayList<CustomerDataModel>> {
        return customerList
    }


}