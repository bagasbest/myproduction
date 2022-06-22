package com.project.myproduction.ui.obat_umum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HerbsViewModel : ViewModel() {

    private val herbList = MutableLiveData<ArrayList<HerbsModel>>()
    private val listData = ArrayList<HerbsModel>()
    private val TAG = HerbsViewModel::class.java.simpleName


    fun setListCommonHerbs() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListCommonHerbsByQuery(query: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .whereGreaterThanOrEqualTo("nameTemp", query)
                .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListCommonHerbsOrderByPriceAscending() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .orderBy("price", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListCommonHerbsOrderByPriceDescending() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .orderBy("price", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListCommonHerbsOrderByStockAscending() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .orderBy("stock", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListCommonHerbsOrderByStockDescending() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .orderBy("stock", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListCommonHerbsOrderByPriceRange(from: Long?, to: Long?) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("common_herbs")
                .whereGreaterThanOrEqualTo("price", from!!)
                .whereLessThanOrEqualTo("price", to!!)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = HerbsModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.size = document.data["size"].toString()
                        model.price = document.data["price"] as Long
                        model.pricePerSize = document.data["pricePerSize"] as? Long
                        model.stock = document.data["stock"] as Long
                        model.stockPerSize = document.data["stockPerSize"] as? Long
                        model.type = document.data["type"].toString()


                        listData.add(model)
                    }
                    herbList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getHerbs() : LiveData<ArrayList<HerbsModel>> {
        return herbList
    }
}