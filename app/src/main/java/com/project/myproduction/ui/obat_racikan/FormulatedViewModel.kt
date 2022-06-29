package com.project.myproduction.ui.obat_racikan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.myproduction.ui.obat_racikan.material.MaterialModel

class FormulatedViewModel : ViewModel() {

    private val formulatedList = MutableLiveData<ArrayList<FormulatedModel>>()
    private val listData = ArrayList<FormulatedModel>()
    private val TAG = FormulatedViewModel::class.java.simpleName


    fun setListFormula() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.type = document.data["type"].toString()
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material

                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListFormulaOrderByUpperPrice() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .orderBy("price", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.type = document.data["type"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material

                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListFormulaOrderByLowerPrice() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .orderBy("price", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.type = document.data["type"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material

                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListFormulaOrderByUpperStock() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .orderBy("stock", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.type = document.data["type"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material

                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListFormulaOrderByLowerStock() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .orderBy("stock", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.type = document.data["type"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material

                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }



    fun setListFormulaOrderByPriceRange(from: Long?, to: Long?) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .whereGreaterThanOrEqualTo("price", from!!)
                .whereLessThanOrEqualTo("price", to!!)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.type = document.data["type"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material


                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListFormulaOrderByQuery(query: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("formulated")
                .whereGreaterThanOrEqualTo("nameTemp", query)
                .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = FormulatedModel()

                        model.code = document.data["code"].toString()
                        model.uid = document.data["uid"].toString()
                        model.name = document.data["name"].toString()
                        model.price = document.data["price"] as Long
                        model.size = document.data["size"].toString()
                        model.nameTemp = document.data["nameTemp"].toString()
                        model.type = document.data["type"].toString()
                        model.material = document.toObject(FormulatedModel::class.java).material


                        listData.add(model)
                    }
                    formulatedList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getFormula() : LiveData<ArrayList<FormulatedModel>> {
        return formulatedList
    }




}