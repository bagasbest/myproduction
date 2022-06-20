package com.project.myproduction.ui.obat_umum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class StockViewModel : ViewModel() {

    private val stockList = MutableLiveData<ArrayList<StockModel>>()
    private val listData = ArrayList<StockModel>()
    private val TAG = StockViewModel::class.java.simpleName


    fun setListStock(from : Long, to : Long) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("single_herb_stocks")
                .whereGreaterThanOrEqualTo("dateInMillis", from)
                .whereLessThanOrEqualTo("dateInMillis", to)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = StockModel()

                        model.date = document.data["date"].toString()
                        model.uid = document.data["uid"].toString()
                        model.dateInMillis = document.data["dateInMillis"] as Long
                        model.data = document.toObject(StockModel::class.java).data


                        listData.add(model)
                    }
                    stockList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getStock() : LiveData<ArrayList<StockModel>> {
        return stockList
    }
}