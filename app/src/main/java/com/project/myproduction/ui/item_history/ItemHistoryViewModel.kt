package com.project.myproduction.ui.item_history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ItemHistoryViewModel : ViewModel() {

    private val itemHistoryList = MutableLiveData<ArrayList<ItemHistoryModel>>()
    private val listData = ArrayList<ItemHistoryModel>()
    private val TAG = ItemHistoryViewModel::class.java.simpleName


    fun setItemHistory() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("item_history")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ItemHistoryModel()

                        model.uid = document.data["uid"].toString()
                        model.date = document.data["date"].toString()
                        model.status = document.data["status"].toString()
                        model.customerName = "" + document.data["customerName"]
                        model.stock = document.data["stock"] as Long

                        listData.add(model)
                    }
                    itemHistoryList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getItemHistory() : LiveData<ArrayList<ItemHistoryModel>> {
        return itemHistoryList
    }
}