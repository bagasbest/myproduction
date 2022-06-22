package com.project.myproduction.ui.obat_racikan.material

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MaterialViewModel : ViewModel() {

    private val materialList = MutableLiveData<ArrayList<MaterialModel>>()
    private val listData = ArrayList<MaterialModel>()
    private val TAG = MaterialViewModel::class.java.simpleName


    fun setListMaterial() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListMaterialOrderByUpperPrice() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .orderBy("price", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListMaterialOrderByLowerPrice() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .orderBy("price", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListMaterialOrderByUpperStock() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .orderBy("stock", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListMaterialOrderByLowerStock() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .orderBy("stock", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListMaterialOrderByPriceRange(from: Long?, to: Long?) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .whereGreaterThanOrEqualTo("price", from!!)
                .whereLessThanOrEqualTo("price", to!!)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListMaterialOrderByQuery(query: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("material")
                .whereGreaterThanOrEqualTo("nameTemp", query)
                .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MaterialModel()

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
                    materialList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getMaterial() : LiveData<ArrayList<MaterialModel>> {
        return materialList
    }




}